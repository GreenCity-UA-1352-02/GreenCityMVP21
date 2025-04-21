package greencity.mapping;

import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UtilsMapperTest {

    @Test
    void mapObject() {

        Source source = new Source(1L, "Test Data");

        Destination destination = UtilsMapper.map(source, Destination.class);

        assertNotNull(destination);
        assertEquals(source.getId(), destination.getId());
        assertEquals(source.getData(), destination.getData());
    }


    @Test
    void mapNullObject() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> UtilsMapper.map(null, Destination.class));
    }

    @Test
    void mapAllToList() {

        List<Source> sourceList = Arrays.asList(
            new Source(1L, "Data 1"),
            new Source(2L, "Data 2"),
            new Source(3L, "Data 3")
        );

        List<Destination> destinationList = UtilsMapper.mapAllToList(sourceList, Destination.class);

        assertNotNull(destinationList);
        assertEquals(sourceList.size(), destinationList.size());
        for (int i = 0; i < sourceList.size(); i++) {
            assertEquals(sourceList.get(i).getId(), destinationList.get(i).getId());
            assertEquals(sourceList.get(i).getData(), destinationList.get(i).getData());
        }
    }

    @Test
    void mapAllToListWithEmptyList() {

        List<Source> sourceList = Collections.emptyList();

        List<Destination> destinationList = UtilsMapper.mapAllToList(sourceList, Destination.class);

        assertNotNull(destinationList);
        assertEquals(0, destinationList.size());
    }


    @Test
    void mapAllToSet() {

        List<Source> sourceList = Arrays.asList(
            new Source(1L, "Data 1"),
            new Source(2L, "Data 2"),
            new Source(1L, "Data 1") // Duplicate
        );

        Set<Destination> destinationSet = UtilsMapper.mapAllToSet(sourceList, Destination.class);

        assertNotNull(destinationSet);
        assertEquals(2, destinationSet.size());
        boolean containsData1 = false;
        boolean containsData2 = false;
        for (Destination dest : destinationSet) {
            if (dest.getId() == 1L && dest.getData().equals("Data 1")) {
                containsData1 = true;
            } else if (dest.getId() == 2L && dest.getData().equals("Data 2")) {
                containsData2 = true;
            }
        }
        assertTrue(containsData1);
        assertTrue(containsData2);
    }

    @Test
    void mapAllToSetWithEmptyList() {

        Set<Destination> destinationSet = UtilsMapper.mapAllToSet(Collections.emptyList(), Destination.class);

        assertNotNull(destinationSet);
        assertEquals(0, destinationSet.size());
    }

    private static class Source {
        private Long id;
        private String data;

        public Source(Long id, String data) {
            this.id = id;
            this.data = data;
        }

        public Long getId() {
            return id;
        }

        public String getData() {
            return data;
        }
    }

    @NoArgsConstructor
    private static class Destination {
        private Long id;
        private String data;


        public Destination(Long id, String data) {
            this.id = id;
            this.data = data;
        }

        public Long getId() {
            return id;
        }

        public String getData() {
            return data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Destination that = (Destination) o;
            return (id == null ? that.id == null : id.equals(that.id)) &&
                (data == null ? that.data == null : data.equals(that.data));
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (data != null ? data.hashCode() : 0);
            return result;
        }
    }
}


