package com.project.trademate.dto.allegro.message;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendMessageRequest {

    private Recipient recipient;
    private String text;
    private List<Attachment> attachments;
    private Order order;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Recipient {
        private String login;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Attachment {
        private String id;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Order {
        private String id;
    }

    public static String createThankYouRateMessage(String orderId) {
        String reviewUrl = "https://allegro.pl/moje-allegro/zakupy/ocen-sprzedawce?orderId=" + orderId;

        return String.format(
                "Dzień dobry,\n\n" +
                        "dziękujemy za zakupy w naszym sklepie i mamy nadzieję, że są Państwo w pełni zadowoleni z otrzymanego produktu.\n\n" +
                        "Państwa opinia jest dla nas niezwykle ważna. Jeśli wszystko jest w porządku, będziemy wdzięczni za poświęcenie chwili na wystawienie pozytywnej oceny sprzedaży. Można to zrobić na stronie:\n" +
                        "%s\n\n" +
                        "W razie jakichkolwiek pytań lub wątpliwości, jesteśmy do Państwa dyspozycji. Prosimy o kontakt zwrotny w tej wiadomości.\n\n" +
                        "Jako nowa, rozwijająca się firma, każda pozytywna ocena jest dla nas na wagę złota i pomaga nam stawać się jeszcze lepszymi.\n\n" +
                        "Pozdrawiam serdecznie,\n" +
                        "Grzegorz Wątroba\n" +
                        "PrzeszkodyHobbyHorse.pl",
                reviewUrl
        );
    }
}
