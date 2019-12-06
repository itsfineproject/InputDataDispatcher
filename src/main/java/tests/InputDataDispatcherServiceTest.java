package tests;

import itsfine.com.inputdatadispatcher.interfaces.IInputDataDispatcher;
import itsfine.com.inputdatadispatcher.service.InputDataDispatcherService;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;


public class InputDataDispatcherServiceTest {
    private IInputDataDispatcher inputDataDispatcher;
    InputDataDispatcherService in = new InputDataDispatcherService(inputDataDispatcher);

    private final LocalDateTime VALID_DATE_TIME = LocalDateTime.now();
    private final LocalDateTime INVALID_DATE_TIME = null;

    private final String VALID_CAR_NUMBER_FORMAT_1 = "11-111-11";
    private final String VALID_CAR_NUMBER_FORMAT_2 = "222-22-222";
    private final String INVALID_CAR_NUMBER_FORMAT_1 = "3-33-333";
    private final String INVALID_CAR_NUMBER_FORMAT_2 = "44a-aa-444";
    private final String CAR_NUMBER_VALIDATION_REG_EX = "\\d{2}-\\d{3}-\\d{2}|\\d{3}-\\d{2}-\\d{3}";

    private final String ARRAY_FOR_SPLIT_SIZE_8 = "[" +
            "{\"parking_id\":d,\"car_number\":\"\",\"date_time\":[2019,12,5,21,8,5,684000000]}," +
            "{\"parking_id\":1,\"car_number\":\"222-22-22\",\"date_time\":[2019,12,5,21,8,5,684000000]}," +
            "{\"parking_id\":5,\"car_number\":\"33-333-33\",\"date_time\":null}," +
            "{\"parking_id\":3,\"car_number\":\"444-44-444\",\"date_time\":[2019,12,5,21,8,5,684000000]}," +
            "{\"parking_id\":5,\"car_number\":\"55-555-55\",\"date_time\":[2019,12,5,21,8,5,684000000]}," +
            "{\"parking_id\":2,\"car_number\":\"666-666-66\",\"date_time\":[2019,12,5,21,8,5,684000000]}," +
            "{\"parking_id\":1,\"car_number\":\"7-7-7\",\"date_time\":[2019,12,5,21,8,5,684000000]}," +
            "{\"parking_id\":3,\"car_number\":\"888888-88\",\"date_time\":[2019,12,5,21,8,5,684000000]}" +
            "]";
    private ArrayList<String> accum = new ArrayList<>();

    @Test
    public void splitArrayByElements() {
        in.splitArrayByElements(new StringBuilder(ARRAY_FOR_SPLIT_SIZE_8), accum);
        int actualSize = accum.size();
        int expectedSize = 8;
        Assert.assertEquals(actualSize, expectedSize);
    }

    @Test
    public void isDateTimeValid() {
        boolean checkTrue = in.isDateTimeValid(VALID_DATE_TIME);
        Assert.assertTrue(checkTrue);

        boolean checkFalse = in.isDateTimeValid(INVALID_DATE_TIME);
        Assert.assertFalse(checkFalse);
    }

    @Test
    public void isMatches() {
        InputDataDispatcherService in = new InputDataDispatcherService(inputDataDispatcher);
        boolean checkTrue1 = in.isMatches(CAR_NUMBER_VALIDATION_REG_EX, VALID_CAR_NUMBER_FORMAT_1);
        boolean checkTrue2 = in.isMatches(CAR_NUMBER_VALIDATION_REG_EX, VALID_CAR_NUMBER_FORMAT_2);
        Assert.assertTrue(checkTrue1);
        Assert.assertTrue(checkTrue2);

        boolean checkFalse1 = in.isMatches(CAR_NUMBER_VALIDATION_REG_EX, INVALID_CAR_NUMBER_FORMAT_1);
        boolean checkFalse2 = in.isMatches(CAR_NUMBER_VALIDATION_REG_EX, INVALID_CAR_NUMBER_FORMAT_2);
        Assert.assertFalse(checkFalse1);
        Assert.assertFalse(checkFalse2);
    }
}