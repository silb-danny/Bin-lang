package Bin;

public class Node <T> {
    public T val;
    private Node<T> next;

    public Node() {
    }

    public Node(T val) {
        this.val = val;
    }

    public Node(T val, Node<T> next) {
        this.val = val;
        this.next = next;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }
}
