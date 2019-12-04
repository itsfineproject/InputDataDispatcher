package itsfine.com.inputdatadispatcher.interfaces;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.MessageChannel;

public interface IInputDataDispatcher extends Processor {
    @Output
    MessageChannel invalid();
}
