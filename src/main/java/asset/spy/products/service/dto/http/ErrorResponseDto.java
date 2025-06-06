package asset.spy.products.service.dto.http;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class ErrorResponseDto {
    private String message;
    private OffsetDateTime time;

    public ErrorResponseDto(String message) {
        this.message = message;
        this.time = OffsetDateTime.now();
    }
}