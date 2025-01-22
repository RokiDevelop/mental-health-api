package com.kiryukhin.mental_health.services.courses;

import com.kiryukhin.mental_health.dtos.requests.CourseTopicRequestDto;
import com.kiryukhin.mental_health.exeptions.EntityNotFoundExceptionCustom;
import com.kiryukhin.mental_health.models.courses.CourseTopic;
import com.kiryukhin.mental_health.repositories.courses.CourseTopicRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseTopicServiceImpl implements CourseTopicService {

    private final CourseTopicRepository courseTopicRepository;


    @Override
    public CourseTopic createCourseTopic(CourseTopic courseTopic) {
        return courseTopicRepository.save(courseTopic);
    }

    @Override
    public Optional<CourseTopic> getCourseTopicById(Long id) {
        return courseTopicRepository.findById(id);
    }

    @Override
    public void deleteCourseTopic(Long id) {
        if (!courseTopicRepository.existsById(id)) {
            throw new ObjectNotFoundException("CourseTopic", id);
        }
        courseTopicRepository.deleteById(id);
    }

    @Override
    public CourseTopic updateCourseTopic(Long id, CourseTopicRequestDto updatedCourseTopic) throws BadRequestException {
        CourseTopic courseTopic = courseTopicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundExceptionCustom(CourseTopic.class));
        try {
            courseTopic.setName(updatedCourseTopic.getName());
            return courseTopicRepository.save(courseTopic);
        } catch (EntityExistsException e) {
            throw new BadRequestException("CourseTopic with name exists: " + updatedCourseTopic.getName());
        } catch (RuntimeException e) {
            throw new BadRequestException("Invalid CourseTopic params: " + updatedCourseTopic.getName());
        }
    }
}
