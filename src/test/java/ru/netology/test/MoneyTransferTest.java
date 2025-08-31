package ru.netology.test;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.DashBoardPage;
import ru.netology.page.LoginPage;
import ru.netology.page.TransferPage;

import static org.junit.jupiter.api.Assertions.assertAll;
import static ru.netology.data.DataHelper.*;

public class MoneyTransferTest {

    private DataHelper.CardInfo firstCardInfo;
    private DataHelper.CardInfo secondCardInfo;

    private int firstCardBalance;
    private int secondCardBalance;

    private DashBoardPage dashBoardPage;

    @BeforeEach
    void setup() {
        Selenide.clearBrowserCookies();

        var info = getAuthInfo();
        var verificationCode = DataHelper.getVerificationCodeFor(info);
        var loginPage = Selenide.open("http://localhost:9999/", LoginPage.class);
        var verificationPage = loginPage.validLogin(info);

        dashBoardPage = verificationPage.validVerify(verificationCode);

        firstCardInfo = DataHelper.getFirstCardInfo();
        secondCardInfo = DataHelper.getSecondCardInfo();

        firstCardBalance = dashBoardPage.getCardBalance(firstCardInfo);
        secondCardBalance = dashBoardPage.getCardBalance(secondCardInfo);
    }

    @Test
    void shouldTransferMoneyFromFirstToSecond() {

        var amount = generateValidAmount(firstCardBalance);
        var expectedBalanceFirstCard = firstCardBalance - amount;
        var expectedBalanceSecondCard = secondCardBalance + amount;

        TransferPage transferPage = dashBoardPage.selectCardToTransfer(secondCardInfo);
        dashBoardPage = transferPage.makeValidTransfer(String.valueOf(amount), firstCardInfo);

        dashBoardPage.reloadDashBoardPage();

        assertAll(
                () -> dashBoardPage.checkCardBalance(firstCardInfo, expectedBalanceFirstCard),
                () -> dashBoardPage.checkCardBalance(secondCardInfo, expectedBalanceSecondCard)
        );
    }

    @Test
    void shouldTransferMoneyFromSecondToFirst() {
        var amount = generateValidAmount(secondCardBalance);
        var expectedBalanceSecondCard = secondCardBalance - amount;
        var expectedBalanceFirstCard = firstCardBalance + amount;
        var transferPage = dashBoardPage.selectCardToTransfer(firstCardInfo);

        dashBoardPage = transferPage.makeValidTransfer(String.valueOf(amount), secondCardInfo);

        dashBoardPage.reloadDashBoardPage();

        assertAll(() -> dashBoardPage.checkCardBalance(firstCardInfo, expectedBalanceFirstCard),
                () -> dashBoardPage.checkCardBalance(secondCardInfo, expectedBalanceSecondCard));
    }

    @Test
    void shouldGetErrorMessageIfAmountMoreBalance() {
        var amount = generateInvalidAmount(secondCardBalance);
        var transferPage = dashBoardPage.selectCardToTransfer(firstCardInfo);

        transferPage.makeTransfer(String.valueOf(amount), secondCardInfo);

        assertAll(() -> transferPage.findErrorMessage("Ошибка! Недостаточно средств на карте для перевода."),
                () -> dashBoardPage.reloadDashBoardPage(),
                () -> dashBoardPage.checkCardBalance(firstCardInfo, firstCardBalance),
                () -> dashBoardPage.checkCardBalance(secondCardInfo, secondCardBalance));
    }

    @Test
    void shouldNotTransferZeroAmount() {
        var amount = "0";

        TransferPage transferPage = dashBoardPage.selectCardToTransfer(secondCardInfo);
        transferPage.makeTransfer(amount, firstCardInfo);

        dashBoardPage.reloadDashBoardPage();

        assertAll(() -> transferPage.findErrorMessage("Ошибка! Сумма для перевода равна нулю."),
                () -> dashBoardPage.checkCardBalance(firstCardInfo, firstCardBalance),
                () -> dashBoardPage.checkCardBalance(secondCardInfo, secondCardBalance));
    }

    @Test
    void shouldShowErrorWhenUsingInvalidCardNumber() {
        var amount = generateValidAmount(firstCardBalance);
        TransferPage transferPage = dashBoardPage.selectCardToTransfer(DataHelper.getFirstCardInfo());

        transferPage.makeTransfer(String.valueOf(amount), DataHelper.getInvalidCardNumber());

        transferPage.findErrorMessage("Ошибка! Произошла ошибка");
    }

    @Test
    void shouldShowErrorWhenFieldsAreEmpty() {
        TransferPage transferPage = dashBoardPage.selectCardToTransfer(DataHelper.getSecondCardInfo());

        transferPage.makeTransfer("", new DataHelper.CardInfo("", ""));

        transferPage.findErrorMessage("Ошибка! Произошла ошибка");
    }


}