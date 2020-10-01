package org.fundacionjala.converter.model.parameter;

import org.fundacionjala.converter.model.ChecksumMD5;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public abstract class ModelParameter {
  private String inputFile;
  private String outputFile;
  private String md5;
  private String format;

  public ModelParameter() {
  }
  /**
   *
   * @param md5
   */
  public void setMd5(final String md5) {
    this.md5 = md5;
  }
  public ModelParameter(final String inputFile, final String outputFile, final String md5) {
    this.inputFile = inputFile;
    this.outputFile = outputFile;
    this.md5 = md5;
  }

  public ModelParameter(final String inputFile, final String outputFile, final String md5, final String format) {
    this.inputFile = inputFile;
    this.outputFile = outputFile;
    this.md5 = md5;
    this.format = format;
  }

  /**
   *
   * @return the format that will be converted
   */
  public String getFormat() {
    return format;
  }

  /**
   *
   * @param format the format that will be seted
   */
  public void setFormat(final String format) {
    this.format = format;
  }

  /**
   * getInputFile
   */
  public String getInputFile() {
    return inputFile;
  }

  /**
   * getOutputFile
   */
  public String getOutputFile() {
    return outputFile;
  }

  /**
   * getMd5
   */
  public String getMd5() {
    String getMd5 = null;
    ChecksumMD5 checksumMD5 = new ChecksumMD5();
    try {
      getMd5 = checksumMD5.getMD5(inputFile);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    return getMd5;
  }

  /**
   * @param inputFile the inputFile to set
   */
  public void setInputFile(final String inputFile) throws IOException {
    this.inputFile = inputFile;
  }

  /**
   * @param outputFile the outputFile to set
   */
  public void setOutputFile(final String outputFile) {
    this.outputFile = outputFile;
  }
}
