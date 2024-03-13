/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package streamprocessor;

import java.util.Arrays;

/**
 * Trivial array based stack.
 *
 * @author Pieter van den Hombergh {@code <pieter.van.den.hombergh@gmail.com>}
 */
class Stack<T> {

    T[] storage = (T[]) new Object[ 4 ];
    int top = -1;

    void push(T p) {
        ensureCapacity();
        storage[ ++top ] = p;
    }

    T pop() {
        return (T) storage[ top-- ];
    }

    T peek() {
        return (T) storage[ top ];
    }

    boolean isEmpty() {
        return top < 0;
    }

    void ensureCapacity() {
        if ( top + 1 >= storage.length ) {
            storage = Arrays.copyOf( storage, storage.length << 1 );
        }
    }
}
