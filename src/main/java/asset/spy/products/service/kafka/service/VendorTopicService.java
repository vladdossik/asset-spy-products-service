package asset.spy.products.service.kafka.service;

import asset.spy.products.service.kafka.config.KafkaProperties;
import asset.spy.products.service.repository.VendorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendorTopicService {

    private final VendorRepository vendorRepository;
    private final KafkaAdmin kafkaAdmin;
    private final KafkaProperties kafkaProperties;

    public List<String> getDynamicTopics(){
        List<String> vendorNames = vendorRepository.findAllVendorNames();

        List<String> topicNames = vendorNames
                .stream()
                .map(String::toLowerCase)
                .map(kafkaProperties.getVendorTopicPrefix()::concat)
                .map(vendorName -> vendorName.replace(" ", "_"))
                .toList();

        createTopics(topicNames);
        return topicNames;
    }

    private void createTopics(List<String> newTopics){
        Map<String, Object> configs = kafkaAdmin.getConfigurationProperties();

        try (AdminClient adminClient = AdminClient.create(configs)) {
            Set<String> existsTopics = adminClient.listTopics().names().get();

            Collection<String> unusedTopicsToDelete = new ArrayList<>();
            for (String existsTopic : existsTopics) {
                if (!newTopics.contains(existsTopic)) {
                    unusedTopicsToDelete.add(existsTopic);
                }
            }
            adminClient.deleteTopics(unusedTopicsToDelete);

            Collection<NewTopic> topicsToCreate = new ArrayList<>();
            for (String topic : newTopics) {
                if (!existsTopics.contains(topic)) {
                    NewTopic newTopic = TopicBuilder
                            .name(topic)
                            .partitions(kafkaProperties.getCountPartitions())
                            .build();
                    topicsToCreate.add(newTopic);
                }
            }
            adminClient.createTopics(topicsToCreate).all().get();
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
        }
    }
}
