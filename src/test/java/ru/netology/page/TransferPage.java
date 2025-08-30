package ru.netology.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class TransferPage {
    private final SelenideElement transferHead = $(byText("Пополнение карты"));
    private final SelenideElement amountInput = $("[data-test-id='amount'] input");
    private final SelenideElement fromInput = $("[data-test-id='from'] input");
    private final SelenideElement transferButton = $("[data-test-id='action-transfer']");
    private final SelenideElement errorMessage = $("[data-test-id='error-notification'] .notification__content");

    public TransferPage() {
        transferHead.shouldBe(visible);
    }

    public void makeTransfer(String amountToTransfer, DataHelper.CardInfo cardInfo) {
        enterAmount(amountToTransfer);
        enterCardNumber(cardInfo.getCardNumber());
        submitTransfer();
    }

    private void enterAmount(String amountToTransfer) {
        amountInput.setValue(amountToTransfer);
    }

    private void enterCardNumber(String cardNumber) {
        fromInput.setValue(cardNumber);
    }

    private void submitTransfer() {
        transferButton.click();
    }

    public DashBoardPage makeValidTransfer(String amountToTransfer, DataHelper.CardInfo cardInfo) {
        makeTransfer(amountToTransfer, cardInfo);
        return new DashBoardPage();
    }

    public void findErrorMessage(String expectedText) {
        errorMessage.should(Condition.and("Ошибка", Condition.text(expectedText), visible));
    }

    public boolean isTransferSuccessful() {
        return !amountInput.is(visible);
    }

}
