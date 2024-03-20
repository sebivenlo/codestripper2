/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.sebivenlo.dependencyfinder;

import java.util.Arrays;
import static java.util.stream.Collectors.joining;
import java.util.stream.IntStream;

/**
 *
 * @author Pieter van den Hombergh {@code <p.vandenhombergh@fontys.nl>}
 */
public class PeekDownStack<E> {

    private E[] storage = (E[]) new Object[ 4 ];
    private int top = -1;

    public PeekDownStack() {
        this( 4 );
    }

    public PeekDownStack( int size ) {
        storage = (E[]) new Object[ size ];
    }

    public void push( E e ) {
        ensureCapacity();
        storage[ ++top ] = e;
    }

    public E peek() {
        return storage[ top ];
    }

    public E pop() {
        E result = storage[ top ];
        top--;
        return result;
    }

    public boolean isEmpty() {
        return top < 0;
    }

    @Override
    public String toString() {
        return IntStream.rangeClosed( 0, top )
                .mapToObj( i -> storage[ i ] )
                .map( e -> e.toString() )
                .collect( joining( ">" ) );
    }

    private void ensureCapacity() {
        if ( top + 1 == storage.length ) {
            storage = Arrays.copyOf( storage, top * 2 );
        }
    }

    /**
     * Peeks below the current top.
     *
     * @param i the distance from the top.
     * @return the value at position i below the top or the bottom element if i
     * would reach beyond the bottom
     */
    public E peekdown( int i ) {
        if ( i < 0 ) {
            i = -1;
        }
        i = Math.min(  i,top);
        return storage[i];
    }
}
