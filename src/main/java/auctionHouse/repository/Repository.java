package auctionHouse.repository;

import auctionHouse.entity.WritableEntity;

import java.io.*;
import java.util.ArrayList;
import java.util.UUID;

public class Repository<E extends WritableEntity> {

    File file;

    public Repository(String filepath) {
        this.file = new File(filepath);
        try {
            this.file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    synchronized public void write(ArrayList<E> entries) throws IOException {

        ArrayList<E> entriesToWrite = new ArrayList<>(entries);
        try {
            ArrayList<E> oldEntries = this.read();
            entriesToWrite.addAll(oldEntries);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

        for(E entry : entriesToWrite){
            objectOutputStream.writeObject(entry);
        }
        fileOutputStream.close();
    }

    synchronized public void cleanFile() throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.close();
    }

    synchronized public ArrayList<E> read() throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

        ArrayList<E> entries = new ArrayList<>();
        while(true){

            try {
                E entry = (E) objectInputStream.readObject();

                if(entry == null) break;

                entries.add(entry);
            }
            catch (Exception e){
//                e.printStackTrace();
                break;
            }

        }
        fileInputStream.close();
        return entries;

    }

    synchronized public E remove(UUID uuid) throws IOException, ClassNotFoundException {
        ArrayList<E> entries = this.read();

        int toDeleteIndex = -1;
        for(int i =0;i<entries.size();i++){
            if(entries.get(i).getUuid().toString().equals(uuid.toString())){
                toDeleteIndex = i;
            }
        }
        if(toDeleteIndex != -1){
            E deletedEntry = entries.remove(toDeleteIndex);
            cleanFile();
            write(entries);
            return deletedEntry;
        }

        return null;
    }

}
