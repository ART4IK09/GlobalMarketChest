package fr.epicanard.globalmarketchest.configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.epicanard.globalmarketchest.GlobalMarketChest;
import fr.epicanard.globalmarketchest.exceptions.CantLoadConfigException;
import fr.epicanard.globalmarketchest.utils.Utils;
import lombok.Getter;

public class ConfigLoader {

  @Getter
  private YamlConfiguration config;
  @Getter
  private YamlConfiguration languages;
  @Getter
  private YamlConfiguration categories;

  public ConfigLoader() {
    this.config = null;
    this.languages = null;
    this.categories =  null;
  }

  /**
   * Save an InputStream inside a file already opened
   * 
   * @param file File opened
   * @param stream Stream to right inside
   * @throws IOException
   */
  private void saveStream(File file, InputStream stream) throws IOException {
    OutputStream out = new FileOutputStream(file);
    byte[] buf = new byte[1024];
    int len;
    while ((len = stream.read(buf)) > 0) {
      out.write(buf, 0, len);
    }
    out.close();
    stream.close();
  }

  /**
   * Load one file from plugin folder and save it if it doesn't exist
   *
   * @throws CantLoadConfigException Throw this exception when the file can't be loaded (InvalidFile or wrong permissions)
   * @param filename Name of the file that must be load
   * @param path Path to get the file inside jar
   * @return Return the yamlconfiguration file
   */
  private YamlConfiguration loadOneFile(String fileName, String path) throws CantLoadConfigException {
    if (!fileName.substring(fileName.length() - 4).equals(".yml"))
      fileName += ".yml";

    final File confFile = new File(GlobalMarketChest.plugin.getDataFolder(), fileName);
    final YamlConfiguration conf = new YamlConfiguration();

    try {
      if (!confFile.exists()) {
        confFile.getParentFile().mkdirs();
        final String filePath = (path != null ? path + "/" : "") + fileName;
        final InputStream stream = GlobalMarketChest.plugin.getResource(filePath);
        this.saveStream(confFile, stream);
      }

      conf.load(confFile);
      return conf;
    } catch (IOException | IllegalArgumentException | InvalidConfigurationException e) {
      throw new CantLoadConfigException(fileName);
    }
  }

  /**
   * Load one file from plugin folder and save it if it doesn't exist
   *
   * @throws CantLoadConfigException Throw this exception when the file can't be loaded (InvalidFile or wrong permissions)
   * @param filename Name of the file that must be load
   * @return Return the yamlconfiguration file
   */
  private YamlConfiguration loadOneFile(String fileName) throws CantLoadConfigException {
    return this.loadOneFile(fileName, null);
  }

  /**
   * Load one resource from jar. It doesn't extract it from the jar
   *
   * @param filename Name of the file that must be load
   * @return Return the yamlconfiguration file
   */
  public YamlConfiguration loadResource(String filename) {
    try {
      InputStream res = GlobalMarketChest.plugin.getResource(filename);
      return YamlConfiguration.loadConfiguration(new InputStreamReader(res));
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  /**
   * Load all necessary resource files
   */
  public void loadFiles()  throws CantLoadConfigException {
    this.config = null;
    this.categories = null;
    this.languages = null;

    this.config = this.loadOneFile("config.yml", Utils.getVersion());
    this.categories = this.loadOneFile("categories.yml", Utils.getVersion());
    if (this.config != null) {
      String tmp = this.config.getString("General.Lang");
      if (tmp == null)
        tmp = "lang-en_EN.yml";
      this.languages = this.loadOneFile(tmp);
    }
  }
}
