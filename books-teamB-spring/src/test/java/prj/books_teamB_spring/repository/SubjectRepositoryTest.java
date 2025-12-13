package prj.books_teamB_spring.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import prj.books_teamB_spring.model.Subject;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SubjectRepositoryTest {

    @Autowired
    private SubjectRepository subjectRepository;

    private Subject testSubject;

    @BeforeEach
    void setUp() {
        subjectRepository.deleteAll();
        testSubject = new Subject();
        testSubject.setLabel("Science Fiction");
        testSubject = subjectRepository.save(testSubject);
    }

    @Test
    void save_shouldPersistSubject() {
        Subject newSubject = new Subject();
        newSubject.setLabel("Fantasy");

        Subject saved = subjectRepository.save(newSubject);

        assertThat(saved.getId()).isNotNull();
        assertThat(subjectRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void findById_shouldReturnSubject_whenSubjectExists() {
        Optional<Subject> found = subjectRepository.findById(testSubject.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getLabel()).isEqualTo("Science Fiction");
    }

    @Test
    void findById_shouldReturnEmpty_whenSubjectDoesNotExist() {
        Optional<Subject> found = subjectRepository.findById(99999L);

        assertThat(found).isEmpty();
    }

    @Test
    void findAll_shouldReturnAllSubjects() {
        Subject subject2 = new Subject();
        subject2.setLabel("Mystery");
        subjectRepository.save(subject2);

        Subject subject3 = new Subject();
        subject3.setLabel("Romance");
        subjectRepository.save(subject3);

        Iterable<Subject> subjects = subjectRepository.findAll();

        assertThat(subjects).hasSize(3);
    }

    @Test
    void deleteById_shouldRemoveSubject() {
        subjectRepository.deleteById(testSubject.getId());

        assertThat(subjectRepository.findById(testSubject.getId())).isEmpty();
    }

    @Test
    void count_shouldReturnCorrectNumber() {
        assertThat(subjectRepository.count()).isEqualTo(1);

        Subject anotherSubject = new Subject();
        anotherSubject.setLabel("Horror");
        subjectRepository.save(anotherSubject);

        assertThat(subjectRepository.count()).isEqualTo(2);
    }
}
