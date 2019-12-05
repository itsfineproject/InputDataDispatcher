package itsfine.com.inputdatadispatcher.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import itsfine.com.inputdatadispatcher.dto.ParkObject;
import itsfine.com.inputdatadispatcher.interfaces.IInputDataDispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.support.MessageBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

@EnableBinding(IInputDataDispatcher.class)
public class InputDataDispatcherService {
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
        ArrayList<ParkObject> parkObjectsList = getParkObjects(parkObjectDataArr);
        for (ParkObject parkObj : parkObjectsList) {
            if (isMatches(parkObj)) {
                inputDataDispatcher.output()
                        .send(MessageBuilder.withPayload(parkObj).build());
            } else {
                inputDataDispatcher.invalid()
                        .send(MessageBuilder.withPayload(parkObj).build());
            }
        }
    }

    private boolean isMatches(ParkObject parkObj) {
        return Pattern.matches(carNumberValidationRegEx, parkObj.getCar_number());
    }

    private ArrayList<ParkObject> getParkObjects(String parkObjectDataArr) throws IOException {
        CollectionType type = mapper.getTypeFactory()
                .constructCollectionType(ArrayList.class, ParkObject.class);
        return mapper.readValue(parkObjectDataArr, type);
    }
}
