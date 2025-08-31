package ru.netology.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class DashBoardPage {

    private final SelenideElement header = $("[data-test-id=dashboard]");
    private ElementsCollection cards = $$(".list__item div");
    private final String balanceStart = "баланс: ";
    private final String balanceFinish = " р.";
    private final SelenideElement reloadButton = $("[data-test-id='action-reload']");

    public DashBoardPage() {
        header.should(visible);
    }

    public void reloadDashBoardPage() {
        reloadButton.click();
        header.shouldBe(visible);
    }

    private SelenideElement getCard(DataHelper.CardInfo cardInfo) {
        return cards.findBy(Condition.attribute("data-test-id", cardInfo.getId()));
    }

    public int getCardBalance(DataHelper.CardInfo cardInfo) {
        var text = getCard(cardInfo).getText();
        return extractBalance(text);
    }

    public TransferPage selectCardToTransfer(DataHelper.CardInfo cardInfo) {
        getCard(cardInfo).$("button").click();
        return new TransferPage();
    }

    private int extractBalance(String text) {
        var start = text.indexOf(balanceStart);
        var finish = text.indexOf(balanceFinish);
        var value = text.substring(start + balanceStart.length(), finish);
        return Integer.parseInt(value);
    }

    public void checkCardBalance(DataHelper.CardInfo cardInfo, int expectedBalance) {
        getCard(cardInfo).should(visible).should(text(balanceStart + expectedBalance + balanceFinish));
    }
}