package com.svirin;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public enum States {
    OK{
        public Circle getState(){
            return new Circle(14, Color.GREEN);
        }
    },
    UNPROCESSED{
        public Circle getState(){
            return new Circle(14, Color.WHITE);
        }
    },
    PROCESSING{
        public Circle getState(){
            return new Circle(14, Color.ORANGE);
        }
    },
    ERROR{
        public Circle getState(){
            return new Circle(14, Color.RED);
        }
    };
    abstract public Circle getState();
}
