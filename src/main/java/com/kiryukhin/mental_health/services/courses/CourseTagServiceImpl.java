package com.kiryukhin.mental_health.services.courses;

import com.kiryukhin.mental_health.dtos.CourseTagDto;
import com.kiryukhin.mental_health.dtos.requests.CourseTagRequestDto;
import com.kiryukhin.mental_health.exeptions.EntityNotFoundExceptionCustom;
import com.kiryukhin.mental_health.mappers.CourseTagMapper;
import com.kiryukhin.mental_health.models.courses.CourseTag;
import com.kiryukhin.mental_health.repositories.courses.CourseTagRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CourseTagServiceImpl implements CourseTagService {

    private final CourseTagRepository courseTagRepository;
    private final CourseTagMapper courseTagMapper;


    @Override
    public CourseTagDto createCourseTag(CourseTagRequestDto courseTagRequestDto) {
        CourseTag courseTag = courseTagMapper.toEntity(courseTagRequestDto);
        return courseTagMapper.toDto(
                courseTagRepository.save(courseTag)
        );
    }
    
    @Override
    public List<CourseTagDto> getAllCourseTags() {
        List<CourseTag> courseTags = courseTagRepository.findAll();
        return courseTags.stream().map(courseTagMapper::toDto).toList();
    }

    @Override
    public CourseTagDto getCourseTagDtoById(Long id) {
        CourseTag courseTag = courseTagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundExceptionCustom(CourseTag.class));
        return courseTagMapper.toDto(courseTag);
    }

    @Override
    public Optional<CourseTag> getCourseTagById(Long id) {
        return courseTagRepository.findById(id);
    }

    @Override
    public Set<CourseTag> getTagsByIdsArray(Long[] ids) {
        return courseTagRepository.findByIdIn(ids);
    }

    @Override
    public void deleteCourseTag(Long id) {
        if (!courseTagRepository.existsById(id)) {
            throw new EntityNotFoundExceptionCustom(CourseTag.class);
        }
        courseTagRepository.deleteById(id);
    }

    @Override
    public CourseTagDto updateCourseTag(Long id, CourseTagRequestDto updatedCourseTag) throws BadRequestException {
        CourseTag courseTag = courseTagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundExceptionCustom(CourseTag.class));
        try {
            courseTagMapper.updatePartial(courseTag, updatedCourseTag);

            return courseTagMapper.toDto(
                    courseTagRepository.save(courseTag)
            );
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid CourseTagType: " + updatedCourseTag.getName());
        }
    }
}
