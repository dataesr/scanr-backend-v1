/*
 * Copyright 2016-2019 MESRI
 * Apache License 2.0
 */
package fr.gouv.recherche.scanr.service;

import fr.gouv.recherche.scanr.api.exception.NotFoundException;
import fr.gouv.recherche.scanr.config.ScreenshotConfiguration;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


@Service
public class ScreenshotStorageService {
    @Autowired
    private ScreenshotConfiguration configuration;

    public void store(String websiteId, byte[] screenshot) {
        File f = asFile(websiteId);

        try (FileOutputStream fos = new FileOutputStream(f, false)) {
            fos.write(screenshot);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public byte[] get(String websiteId) throws NotFoundException {
        File f = asFile(websiteId);
        if (!f.exists()) throw new NotFoundException("screenshot", websiteId);

        try (FileInputStream fos = new FileInputStream(f)) {
            return IOUtils.toByteArray(fos);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    protected File asFile(String websiteId) {
        if (websiteId.length() > 150) {
            websiteId = websiteId.substring(0, 150);
        }
        String filename = Base64Utils.encodeToUrlSafeString(websiteId.getBytes(Charsets.UTF_8));
        return new File(configuration.getStoragePath(), filename);
    }
}
