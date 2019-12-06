package itsfine.com.inputdatadispatcher.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import itsfine.com.inputdatadispatcher.dto.ParkObject;
import itsfine.com.inputdatadispatcher.interfaces.IInputDataDispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.support.MessageBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;

@EnableBinding(IInputDataDispatcher.class)
public class InputDataDispatcherService {
    private ArrayList<String> elementsFromArray = new ArrayList<>();
    private ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
    //regex for israel car numbers (7 digits with hyphen after second and 5th digit  (1980-2017),
    // 8 digits with hyphen after 3th and 5th digit (2017-now))
    @Value("${car_number_validation_reg_ex:\\d{2}-\\d{3}-\\d{2}|\\d{3}-\\d{2}-\\d{3}}")
    private String carNumberValidationRegEx;

    final IInputDataDispatcher inputDataDispatcher;

    public InputDataDispatcherService(IInputDataDispatcher inputDataDispatcher) {
        this.inputDataDispatcher = inputDataDispatcher;
    }

    @StreamListener(IInputDataDispatcher.INPUT)
    public void dispatchInputData(String parkObjectDataArr) throws IOException {
        ParkObject[] parkObjects = new ParkObject[0];
        try {
            parkObjects = mapper.readValue(parkObjectDataArr, ParkObject[].class);
        } catch (JsonProcessingException e) {
            splitArrayByElements(new StringBuilder(parkObjectDataArr), elementsFromArray);
            handleElements();
            elementsFromArray.clear();
        }
        if (parkObjects.length > 0) {
            for (ParkObject parkObj : parkObjects) {
                sendMessages(parkObj);
            }
        }
    }

    public void splitArrayByElements(StringBuilder parkObjectDataArr, ArrayList<String> accum) {
        int firstIndex = parkObjectDataArr.indexOf("{");
        int lastIndex = parkObjectDataArr.indexOf("}") + 1;
        if (firstIndex > 0) {
            accum.add(parkObjectDataArr.substring(firstIndex, lastIndex));
            splitArrayByElements(parkObjectDataArr.delete(firstIndex, lastIndex), accum);
        }
    }

    private void handleElements() {
        for (String str : elementsFromArray) {
            try {
                ParkObject parkObject = mapper.readValue(str, ParkObject.class);
                sendMessages(parkObject);
            } catch (JsonProcessingException e) {
                inputDataDispatcher.invalid()
                        .send(MessageBuilder.withPayload(str).build());
            }
        }
    }

    private void sendMessages(ParkObject parkObj) throws JsonProcessingException {
        if (isMatches(carNumberValidationRegEx ,parkObj.getCar_number()) && isDateTimeValid(parkObj.getDate_time())) {
            inputDataDispatcher.output()
                    .send(MessageBuilder.withPayload(mapper.writeValueAsString(parkObj)).build());
        } else {
            inputDataDispatcher.invalid()
                    .send(MessageBuilder.withPayload(mapper.writeValueAsString(parkObj)).build());
        }
    }

    public boolean isDateTimeValid(LocalDateTime date_time) {
        return !Objects.isNull(date_time);
    }

    public boolean isMatches(String regEx ,String carNumber) {
        return Pattern.matches(regEx, carNumber);
    }
}
