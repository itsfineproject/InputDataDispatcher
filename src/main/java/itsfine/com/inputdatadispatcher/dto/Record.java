package itsfine.com.inputdatadispatcher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Record {
    String parking_id;
    String car_number;
    LocalDate date_time;
}
