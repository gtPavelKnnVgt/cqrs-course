package cqrs.my.demo.course.query;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/appointments")
@RequiredArgsConstructor
public class QueryController {
    private final QueryRepository queryRepository;

    @GetMapping
    List<Appointment> getAppointmentsWithPatientAndDoctor(FindAppointments query) {
       return queryRepository.findAppointments(query.doctorFirstName(), query.doctorLastName(), query.offset(), query.limit());
    }
}
