package asset.spy.products.service.kafka.service;

import asset.spy.products.service.dto.kafka.ProductItemDto;
import asset.spy.products.service.event.CreatedNewVendorEvent;
import asset.spy.products.service.event.DeletedVendorEvent;
import asset.spy.products.service.event.UpdatedVendorEvent;
import asset.spy.products.service.kafka.config.KafkaProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpoint;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class DynamicProductItemListenerCreatorService {
    private static final AtomicLong ENDPOINT_ID_INDEX = new AtomicLong(1);
    private static String listenerId;
    private final VendorTopicService vendorTopicService;
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final ConcurrentKafkaListenerContainerFactory<String, ProductItemDto> kafkaListenerContainerFactory;
    private final ProductItemListenerService productItemListenerService;
    private final KafkaProperties kafkaProperties;

    public DynamicProductItemListenerCreatorService(VendorTopicService vendorTopicService,
                                                    @Qualifier("kafkaListenerEndpointRegistry")
                                                    KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry,
                                                    ConcurrentKafkaListenerContainerFactory<String, ProductItemDto> kafkaListenerContainerFactory,
                                                    ProductItemListenerService productItemListenerService,
                                                    KafkaProperties kafkaProperties) {
        this.vendorTopicService = vendorTopicService;
        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
        this.kafkaListenerContainerFactory = kafkaListenerContainerFactory;
        this.productItemListenerService = productItemListenerService;
        this.kafkaProperties = kafkaProperties;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startDynamicListener() {
        List<String> topics = vendorTopicService.getDynamicTopics();

        if (topics.isEmpty()) {
            log.warn("Topics not found. Consumers will not be started.");
        } else {
            KafkaListenerEndpoint listener = createKafkaListenerEndpoint(topics);
            kafkaListenerEndpointRegistry.registerListenerContainer(listener, kafkaListenerContainerFactory, true);
            listenerId = listener.getId();
            log.info("Created ProductItemListener with id {}", listenerId);
        }
    }

    @EventListener({CreatedNewVendorEvent.class, UpdatedVendorEvent.class, DeletedVendorEvent.class})
    public void updateDynamicListener() {
        if (listenerId == null) {
            log.info("Creating ProductItemListener");
            startDynamicListener();
        } else {
            log.info("Updating ProductItemListener by new topics with id {}", listenerId);
            stopListenerEndpoint(listenerId);
            kafkaListenerEndpointRegistry.unregisterListenerContainer(listenerId);
            startDynamicListener();
            log.info("Updated ProductItemListener by new topics");
        }
    }

    private KafkaListenerEndpoint createKafkaListenerEndpoint(List<String> topics) {
        MethodKafkaListenerEndpoint<String, ProductItemDto> kafkaListenerEndpoint = new MethodKafkaListenerEndpoint<>();
        kafkaListenerEndpoint.setId(generateListenerId());
        kafkaListenerEndpoint.setGroupId(kafkaProperties.getGroupId());
        kafkaListenerEndpoint.setAutoStartup(true);
        kafkaListenerEndpoint.setTopics(topics.toArray(new String[0]));
        kafkaListenerEndpoint.setMessageHandlerMethodFactory(new DefaultMessageHandlerMethodFactory());
        kafkaListenerEndpoint.setBean(productItemListenerService);

        try {
            kafkaListenerEndpoint.setMethod(ProductItemListenerService.class.getMethod("onMessage", ConsumerRecord.class));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Attempt to call a non-existent method");
        }

        return kafkaListenerEndpoint;
    }

    private String generateListenerId() {
        return kafkaProperties.getListenerPrefix() + ENDPOINT_ID_INDEX.getAndIncrement();
    }

    private void stopListenerEndpoint(String listenerId) {
        MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry
                .getListenerContainer(listenerId);
        if (listenerContainer != null && listenerContainer.isRunning()) {
            listenerContainer.stop();
        } else {
            log.warn("Listener with id {} can't be stopped", listenerId);
        }
    }
}
