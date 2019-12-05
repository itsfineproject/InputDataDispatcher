package itsfine.com.inputdatadispatcher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParkObject {
    private int parking_id;
    private String car_number;
    private LocalDateTime date_time;
}
