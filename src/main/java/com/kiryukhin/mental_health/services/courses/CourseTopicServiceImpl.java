package com.kiryukhin.mental_health.services.courses;

import com.kiryukhin.mental_health.dtos.CourseTopicDto;
import com.kiryukhin.mental_health.dtos.requests.CourseTopicRequestDto;
import com.kiryukhin.mental_health.exeptions.EntityNotFoundExceptionCustom;
import com.kiryukhin.mental_health.mappers.CourseTopicMapper;
import com.kiryukhin.mental_health.models.courses.CourseTopic;
import com.kiryukhin.mental_health.repositories.courses.CourseTopicRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseTopicServiceImpl implements CourseTopicService {

    private final CourseTopicRepository courseTopicRepository;
    private final CourseTopicMapper courseTopicMapper;


    @Override
    public CourseTopicDto createCourseTopic(CourseTopicRequestDto courseTopicRequestDto) {
        CourseTopic courseTopic = courseTopicMapper.toEntity(courseTopicRequestDto);
        return courseTopicMapper.toDto(
                courseTopicRepository.save(courseTopic)
        );
    }

    @Override
    public List<CourseTopicDto> getAllCourseTopics() {
        List<CourseTopic> courseTopics = courseTopicRepository.findAll();
        return courseTopics.stream().map(courseTopicMapper::toDto).toList();
    }

    @Override
    public Optional<CourseTopic> getCourseTopicById(Long id) {
        return courseTopicRepository.findById(id);
    }

    @Override
    public CourseTopicDto getCourseTopicDtoById(Long id) {
        CourseTopic courseTopic = courseTopicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundExceptionCustom(CourseTopic.class));
        return courseTopicMapper.toDto(courseTopic);
    }

    @Override
    public void deleteCourseTopic(Long id) {
        if (!courseTopicRepository.existsById(id)) {
            throw new ObjectNotFoundException("CourseTopic", id);
        }
        courseTopicRepository.deleteById(id);
    }

    @Override
    public CourseTopicDto updateCourseTopic(Long id, CourseTopicRequestDto updatedCourseTopic) throws BadRequestException {
        CourseTopic courseTopic = courseTopicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundExceptionCustom(CourseTopic.class));
        try {
            courseTopicMapper.updatePartial(courseTopic, updatedCourseTopic);

            return courseTopicMapper.toDto(
                    courseTopicRepository.save(courseTopic)
            );
        } catch (EntityExistsException e) {
            throw new BadRequestException("CourseTopic with name exists: " + updatedCourseTopic.getName());
        } catch (RuntimeException e) {
            throw new BadRequestException("Invalid CourseTopic params: " + updatedCourseTopic.getName());
        }
    }
}
