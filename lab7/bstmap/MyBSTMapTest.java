package bstmap;

import org.junit.Test;

import static org.junit.Assert.*;

public class MyBSTMapTest {
    @Test
    public void testRemoveRoot() {
        BSTMap<String, String> q = new BSTMap<String, String>();
        q.put("c", "a");
        q.put("b", "a");
        q.put("a", "a");
        q.put("d", "a");
        q.put("e", "a"); // a b c d e
        q.printInOrder();
        assertNotNull(q.remove("c"));
        assertFalse(q.containsKey("c"));
        assertTrue(q.containsKey("a"));
        assertTrue(q.containsKey("b"));
        assertTrue(q.containsKey("d"));
        assertTrue(q.containsKey("e"));
    }

    @Test
    public void testRemoveThreeCases() {
        BSTMap<String, String> q = new BSTMap<String, String>();
        q.put("c", "a");
        q.put("b", "a");
        q.put("a", "a");
        q.put("d", "a");
        q.put("e", "a");                         // a b c d e

        assertNotNull(q.remove("e"));      // a b c d
        assertTrue(q.containsKey("a"));
        assertTrue(q.containsKey("b"));
        assertTrue(q.containsKey("c"));
        assertTrue(q.containsKey("d"));

        assertNotNull(q.remove("c"));      // a b d
        assertTrue(q.containsKey("a"));
        assertTrue(q.containsKey("b"));
        assertTrue(q.containsKey("d"));

        q.put("f", "a");                         // a b d f

        assertNotNull(q.remove("d"));      // a b f
        assertTrue(q.containsKey("a"));
        assertTrue(q.containsKey("b"));
        assertTrue(q.containsKey("f"));
    }

    @Test
    public void testRemoveRootEdge() {
        BSTMap rightChild = new BSTMap();
        rightChild.put('A', 1);
        rightChild.put('B', 2);
        Integer result = (Integer) rightChild.remove('A');
        assertTrue(result.equals(new Integer(1)));
        // <B: 2>

        for (int i = 0; i < 10; i++) {
            rightChild.put((char) ('C' + i), 3 + i);
        }
        // <B: 2> <C: 3> <D: 4> <E: 5> <F: 6> <G: 7> <H: 8> <I: 9> <J: 10> <K: 11> <L: 12>

        rightChild.put('A', 100);
        // <A: 100> <B: 2> <C: 3> <D: 4> <E: 5> <F: 6> <G: 7> <H: 8> <I: 9> <J: 10> <K: 11> <L: 12>

        assertTrue(((Integer) rightChild.remove('D')).equals(new Integer(4)));
        assertTrue(((Integer) rightChild.remove('G')).equals(new Integer(7)));
        assertTrue(((Integer) rightChild.remove('A')).equals(new Integer(100)));
        rightChild.printInOrder();
        assertTrue(rightChild.size() == 9);
        //<B: 2> <C: 3> <E: 5> <F: 6> <H: 8> <I: 9> <J: 10> <K: 11> <L: 12>

        BSTMap leftChild = new BSTMap();
        leftChild.put('B', 1);
        leftChild.put('A', 2);
        assertTrue(((Integer) leftChild.remove('B')).equals(1));
        assertEquals(1, leftChild.size());
        assertEquals(null, leftChild.get('B'));

        BSTMap noChild = new BSTMap();
        noChild.put('Z', 15);
        assertTrue(((Integer) noChild.remove('Z')).equals(15));
        assertEquals(0, noChild.size());
        assertEquals(null, noChild.get('Z'));
    }

    @Test
    public void iterTest() {
        BSTMap<String, String> q = new BSTMap<String, String>();
        q.put("c", "a");
        q.put("b", "a");
        q.put("a", "a");
        q.put("d", "a");
        q.put("e", "a");

        for (var item : q) {
            System.out.println(item);
        }
    }
}
