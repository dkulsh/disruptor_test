package disruptor;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.UUID;
import java.util.concurrent.Executors;

public class Simple {

    public static void main(String[] args) {

        Disruptor<ValueEvent> disruptor = new Disruptor<ValueEvent>(ValueEvent.EVENT_FACTORY,
                64, Executors.defaultThreadFactory());

        final EventHandler<ValueEvent> one = (valueEvent, l, b) -> {

            System.out.println("ONE HANDLER :: " + l);
            System.out.println("Sequence: " + l);
            System.out.println("ValueEvent: " + valueEvent.getValue());

            Thread.sleep(100);
        };

        final EventHandler<ValueEvent> two = (valueEvent, l, b) -> {

            System.out.println("TWO HANDLER :: " + l);

            Thread.sleep(100);
            System.out.println("Sequence: " + l);
            System.out.println("ValueEvent: " + valueEvent.getValue());
        };

//        disruptor.handleEventsWith(one).then(two);
        disruptor.handleEventsWith(one);

        RingBuffer<ValueEvent> ringBuffer = disruptor.start();

        for (int i = 0; i < 2000; i++) {

            long seq = ringBuffer.next();
            System.out.println("RingBuffer Next :: " + seq);

            ValueEvent valueEvent = ringBuffer.get(seq);
            valueEvent.setValue(UUID.randomUUID().toString());

            System.out.println("Available Capacity ::" + ringBuffer.hasAvailableCapacity(1));
            ringBuffer.publish(seq);
        }

        disruptor.shutdown();

    }
}