package cqrs.my.demo.course.command;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.created;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("api/v1/appointments")
@RequiredArgsConstructor
public class CommandController {
    private final CommandHandler commandHandler;

    @PostMapping
    ResponseEntity<?> createAppointment(@RequestBody @Valid DoctorCommand.CreateAppointment createAppointment) {
        var appointmentId = commandHandler.handle(createAppointment).orElse(null);

        return created(fromCurrentRequest().path("/appointments/{id}").buildAndExpand(appointmentId).toUri()).build();
    }
}
