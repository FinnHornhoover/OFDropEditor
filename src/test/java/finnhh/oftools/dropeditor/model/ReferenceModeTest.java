package finnhh.oftools.dropeditor.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ReferenceModeTest {
    @Test
    void testForSize() {
        Assertions.assertSame(ReferenceMode.NONE, ReferenceMode.forSize(-1));
        Assertions.assertSame(ReferenceMode.NONE, ReferenceMode.forSize(0));
        Assertions.assertSame(ReferenceMode.UNIQUE, ReferenceMode.forSize(1));
        Assertions.assertSame(ReferenceMode.MULTIPLE, ReferenceMode.forSize(2));
    }
}
