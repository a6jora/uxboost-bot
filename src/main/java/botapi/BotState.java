package botapi;

public enum BotState {
    ASK_START,
    ASK_OPTION,
    ASK_AD,
    ASK_GLADS,
    ASK_TIME_FOR,
    ASK_TIME_SLOTS,
    ASK_REST,
    ASK_CONTACTS,
    ASK_DEADLINE,
    ASK_TO_POST,
    ASK_TO_SEND
}
// questions.add("1. Пожелания к респондентам и что хотите сделать на созвоне.");
//         questions.add("2. Время, которое по вашему мнению нужно на интервью/опрос/тест — чтобы ваш респондент мог планировать свой график");
//         questions.add("3. Временные слоты в которые хотите провести созвон — чтобы другим участникам сразу было легче ориентироваться");
//         questions.add("4. Если для вас важно гео, или есть другие ограничения — не забудьте указать");
//         questions.add("5. Контакты — куда откликаться, где заполнять вашу анкету и пр.");
//         questions.add("6. До какого числа актуально");