package itsfine.com.inputdatadispatcher.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import itsfine.com.inputdatadispatcher.dto.Record;
import itsfine.com.inputdatadispatcher.interfaces.IInputDataDispatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.support.MessageBuilder;

import java.io.IOException;
import java.util.regex.Pattern;

@EnableBinding(Processor.class)
public class InputDataDispatcherService {
    private ObjectMapper mapper = new ObjectMapper();
    //regex for israel car numbers (7 digits with hyphen after second and 5th digit  (1980-2017),
    // 8 digits with hyphen after 3th and 5th digit (2017-now))
    @Value("${car_number_validation_reg_ex:(\\d{2}-\\d{3}-\\d{2}|\\d{3}-\\d{2}-\\d{3})")
    private String carNumberValidationRegEx;
    @Autowired
    IInputDataDispatcher inputDataDispatcher;

    @StreamListener(IInputDataDispatcher.INPUT)
    public void dispatchInputData(String recordStrData) throws IOException {
        Record record = mapper.readValue(recordStrData, Record.class);
        if (Pattern.matches(carNumberValidationRegEx, record.getCar_number())) {
            inputDataDispatcher.output()
                    .send(MessageBuilder.withPayload(recordStrData).build());
        } else {
            inputDataDispatcher.invalid()
                    .send(MessageBuilder.withPayload(recordStrData).build());
        }
    }
}
