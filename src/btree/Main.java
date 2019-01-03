/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package btree;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 *
 * @author Pawel
 */
public class Main {

    public static void main(String[] args) {
        try {
            Scanner sc;
            sc = new Scanner(System.in);
            int d = 2; //sc.nextInt();
            
            BtreeBase btree = new Btree();
            String indexFile = "indeks.txt";
            String mainFile = "main.txt";
            int r = 10;
            int e = 1;
            btree.open(indexFile, mainFile, d, r, e);

        
            if (args.length == 1) {
                sc = new Scanner(new FileReader(args[0]));
            } else {
                sc = new Scanner(System.in);
            }
            String line = sc.next();
            long key = 0;
            double time = 0;
            try {
                while (line.charAt(0) != 'x') {
                    switch (line.charAt(0)) {
                        case 'i': //umieszczenie
                            key = sc.nextLong();
                            time = sc.nextDouble();
                            // todo dac rand
                            if (key >= 0) {
                                btree.insert(key, new Record(time));
                            } else {
                                System.out.println("Klucz musi byc >= 0 !");
                            }
                            break;
                        case 'u': //aktualizacja
                            key = sc.nextLong();
                            time = sc.nextDouble();
                            btree.update(key, new Record(time));
                            break;
                        case 's': //wyszukiwanie
                            key = sc.nextLong();
                            btree.search(key);
                            break;
                        case 'd': //usuniecie
                            key = sc.nextLong();
                            btree.delete(key);
                            break;
                        case 'e': //wyswietla odczyty i zapisy
                            btree.printReadsAndWrites();
                            break;
                        case 'p': //wyswietla zawartosc pliku
                            btree.readEntireFile();
                            break;
                        case 'f': //czysci bufory
                            btree.flush();
                            btree.clearCounters();
                            break;
                        case 'R':
                            btree.readEntireFile();
                            break;
                    }
                    line = sc.next();
                }
            } catch (InputMismatchException ex) {
                System.out.println("Nie ma takiej opcji");
            }
            btree.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Zly plik");
        }
    }
}
