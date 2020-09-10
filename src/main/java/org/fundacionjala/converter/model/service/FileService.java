/**
 * Copyright (c) 2020 Fundacion Jala.
 *
 * This software is the confidential and proprietary information of Fundacion Jala
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with Fundacion Jala
 */
package org.fundacionjala.converter.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.NonUniqueResultException;

import org.fundacionjala.converter.model.entity.File;
import org.fundacionjala.converter.model.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;

/**
 * @author Laura Montaño
 * @version 0.1
 */
@Service
public class FileService {
    @Autowired
    private FileRepository fileRepository;

    /**
     * Returns all the elements in the table files
     * @return fileList - The list of elements, if there is no element, returns an empty list
     */
    public List<File> getFileList() {
        List<File> fileList = new ArrayList<File>();
        for (File file : fileRepository.findAll()) {
            fileList.add(file);
        }
        return fileList;
    }

    /**
     * Returns element asociated to the given id
     * @param index - the Long id of element to show
     * @return file - a reference to the file from the table
     */
    public File getFileById(final int index) {
        Optional<File> file = fileRepository.findById((long) index);
        if (file.isPresent()) {
            return file.get();
        } else {
            return null;
        }
    }

    /**
     * Returns element asociated to the given md5
     * @param md5 - String md5 of element to show
     * @return file - a reference to the file from the table
     */
    public File getFileByMd5(final String md5) {
        File file = fileRepository.findByMd5(md5);
        return file;
    }

    /**
     * Saves a file in the database
     * @param file - the reference to the object File to save
     */
    public void saveFile(final File file) {
    File fileTemp = new File();
    try {
        if (file.getMd5() != null && file.getPath() != null) {
            fileTemp.setPath(file.getPath());
            fileTemp.setMd5(file.getMd5());
            fileRepository.save(fileTemp);
        }
    } catch (NullPointerException e) {
        e.printStackTrace();
        System.out.println("NullPointerException ocurred");
    }
    }

    /**
     * Updates a file in the table "files"
     * @param file - the reference to the object File to update
     */
    public void updateFile(final File file) {
    try {
        if (file.getMd5() != null && file.getPath() != null) {
            File fileTemp = fileRepository.findByMd5(file.getMd5());
            fileTemp.setPath(file.getPath());
            fileTemp.setMd5(file.getMd5());
            fileRepository.save(fileTemp);
        }
    } catch (NullPointerException | NonUniqueResultException | IncorrectResultSizeDataAccessException e) {
        e.printStackTrace();
        System.out.println("Error ocurred");
    }
    }

    /**
     * Deletes the reference to a file in the table "files"
     * @param file - the reference to the object File to delete
     */
    public void deleteFile(final File file) {
        File fileToDelete = fileRepository.findByMd5(file.getMd5());
        fileRepository.delete(fileToDelete);
    }

    /**
     * Deletes a file in the table "files"
     * @param md5 - the String md5 of the file to delete
     */
    public void deleteFileByMd5(final String md5) {
        File fileToDelete = fileRepository.findByMd5(md5);
        fileRepository.delete(fileToDelete);
    }
}
