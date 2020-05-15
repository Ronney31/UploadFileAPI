package com.rnkuSpringPractice.demo.storage;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService {

	private final String fileLocation = "upload-dir";
	private final Path rootLocation = Paths.get(fileLocation);

	// @Override
	public void store(MultipartFile file) {
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
			}
			Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()));
		} catch (IOException e) {
			throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
		}
	}
	
	// @Override
		public String storeFile(MultipartFile file) {
			try {
				if (file.isEmpty()) {
					return "Failed to store empty file " + file.getOriginalFilename();
				}
				Files.copy(file.getInputStream(), this.rootLocation.resolve(file.getOriginalFilename()));
			} catch (IOException e) {
				return "Failed to store file " + file.getOriginalFilename();
			}
			return "File Successfully Uploaded !";
		}

	/*
	 * To load all the files present in directory.
	 */
	// @Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1).filter(path -> !path.equals(this.rootLocation))
					.map(path -> this.rootLocation.relativize(path));
		} catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}

	}

	// @Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = rootLocation.resolve(filename);
			;
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				System.out.println("Could not read file: " + filename);
				throw new StorageException("Could not read file: " + filename);
			}
		} catch (MalformedURLException e) {
			System.out.printf("Could not read file: " + filename, e);
			throw new StorageException("Could not read file: " + filename, e);
		}
	}
}
