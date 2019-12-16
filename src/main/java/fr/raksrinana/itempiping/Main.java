package fr.raksrinana.itempiping;

import net.minecraft.item.DyeColor;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class Main{
	public static void main(String[] args){
		duplicate(Paths.get("D:\\Documents\\Programming\\IntelliJ\\Pipes\\src\\main\\resources\\assets\\item_piping\\blockstates"));
		duplicate(Paths.get("D:\\Documents\\Programming\\IntelliJ\\Pipes\\src\\main\\resources\\assets\\item_piping\\models\\block"));
		duplicate(Paths.get("D:\\Documents\\Programming\\IntelliJ\\Pipes\\src\\main\\resources\\assets\\item_piping\\models\\item"));
		duplicate(Paths.get("D:\\Documents\\Programming\\IntelliJ\\Pipes\\src\\main\\resources\\assets\\item_piping\\textures\\block"));
	}
	
	public static void duplicate(Path inputPath){
		Collection<Path> baseFiles = Optional.ofNullable(inputPath.toFile().listFiles()).map(files -> Arrays.stream(files).filter(file -> file.getName().startsWith("c_pipe")).map(file -> Paths.get(file.toURI())).collect(Collectors.toList())).orElse(new ArrayList<>());
		Arrays.stream(DyeColor.values()).forEach(dyeColor -> baseFiles.forEach(baseFile -> {
			System.out.println("Duplication " + baseFile + " for color " + dyeColor.getName());
			Path targetPath = baseFile.getParent().resolve(baseFile.getFileName().toString().replace("c_", dyeColor.getName() + "_"));
			if(targetPath.getFileName().toString().endsWith(".json")){
				try(BufferedWriter bw = Files.newBufferedWriter(targetPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)){
					Files.readAllLines(baseFile).forEach(baseLine -> {
						try{
							bw.append(baseLine.replace("c_", dyeColor.getName() + "_"));
							bw.newLine();
						}
						catch(IOException e){
							e.printStackTrace();
						}
					});
				}
				catch(IOException e){
					e.printStackTrace();
				}
			}
			else{
				try{
					Files.copy(baseFile, targetPath, StandardCopyOption.COPY_ATTRIBUTES);
				}
				catch(IOException e){
					e.printStackTrace();
				}
			}
		}));
	}
}
