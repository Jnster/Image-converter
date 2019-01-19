package ru.jnster;

public interface Artist {  //TODO: Решить по поводу уместности интерфейсов в данном случае
    void applyMasksOnFull();
    void openFile(String pathFile);
    void saveTo(String pathFile);
}
