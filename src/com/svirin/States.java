package com.svirin;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
//Перерахування станів файлу
public enum States {
    OK{//Підрахунок\перевірка хеш-суми пройшла успішно. Виводиться як коло зеленого кольору, радіусом 14 пікселів
        public Circle getState(){
            return new Circle(14, Color.GREEN);
        }
    },
    UNPROCESSED{//Підрахунок\перевірка хеш-суми не починалась. Виводиться як коло білого кольору, радіусом 14 пікселів
        public Circle getState(){
            return new Circle(14, Color.WHITE);
        }
    },
    PROCESSING{//Підрахунок\перевірка хеш-суми відбувається. Виводиться як коло помаранчевого кольору, радіусом 14 пікселів
        public Circle getState(){
            return new Circle(14, Color.ORANGE);
        }
    },
    ERROR{//Сталася помилка. Виводиться як коло червоного кольору, радіусом 14 пікселів
        public Circle getState(){
            return new Circle(14, Color.RED);
        }
    };
    //Абстрактний метод, що повертає коло певного кольору. Має різну реалізацію в кожному зі станів.
    abstract public Circle getState();
}
