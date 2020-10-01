package org.fundacionjala.converter.controller;

import org.fundacionjala.converter.controller.request.RequestAudioParameter;
import org.fundacionjala.converter.controller.response.ErrorResponse;
import org.fundacionjala.converter.controller.response.OkResponse;
import org.fundacionjala.converter.controller.service.FileZipped;
import org.fundacionjala.converter.executor.Executor;
import org.fundacionjala.converter.model.command.AudioModel;
import org.fundacionjala.converter.model.command.ICommand;
import org.fundacionjala.converter.database.entity.File;
import org.fundacionjala.converter.model.parameter.multimedia.AudioParameter;
import org.fundacionjala.converter.controller.service.FileService;
import org.fundacionjala.converter.controller.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Mirko Romay
 * @version 0.1
 */
@RestController
public class AudioController {

    @Autowired
    private FileService fileService;
    @Value("${convertedFiles.path}")
    private String output;
    @Autowired
    private FileUploadService fileUploadService;

    /**
     *
     * @param requestAudioParameter
     * @return
     * @throws Exception
     */

    @RequestMapping(value = "convertAudio", method = RequestMethod.POST)
    public ResponseEntity audio(final RequestAudioParameter requestAudioParameter) {
        try {
            requestAudioParameter.validate();
            String md5 = requestAudioParameter.getMd5();
            String filePath = "";

            if (fileService.getFileByMd5(md5) == null) {
                filePath = fileUploadService.saveInputFile(requestAudioParameter.getFile());
                fileService.saveFile(new File(filePath, md5));
            } else {
                filePath = fileService.getFileByMd5(md5).getPath();
            }

            AudioParameter audioParameter = new AudioParameter();
            setAudioParameterValues(audioParameter, requestAudioParameter, filePath);
            String result = FileZipped.zipper(audioParameter, execute(audioParameter));
            return ResponseEntity.ok().body(new OkResponse<Integer>(HttpServletResponse.SC_OK, result));
        } catch (IOException | InterruptedException | ExecutionException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse<Integer>(HttpServletResponse.SC_BAD_REQUEST, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse<String>(Integer.toString(HttpServletResponse.SC_BAD_REQUEST), e.getMessage()));
        }
    }

    /**
     *
     * @param audioParameter
     * @param requestAudioParameter
     * @param filePath
     * @throws IOException
     */
    private void setAudioParameterValues(final AudioParameter audioParameter, final RequestAudioParameter requestAudioParameter, final String filePath) throws IOException {
        audioParameter.setInputFile(filePath);
        audioParameter.setOutputFile(output);
        audioParameter.setMd5(requestAudioParameter.generateMD5(filePath));
        audioParameter.setName(requestAudioParameter.getName());
        audioParameter.setFormat(requestAudioParameter.getExportFormat());
        audioParameter.setCodec(requestAudioParameter.getCodec());
        audioParameter.setBitRate(requestAudioParameter.getBitRate()); // -b:a
        audioParameter.setChannel(requestAudioParameter.getChannel()); // stereo
        audioParameter.setSampleRate(requestAudioParameter.getSampleRate()); //-ar
        audioParameter.setStart(requestAudioParameter.getStart()); //-ss
        audioParameter.setDuration(requestAudioParameter.getDuration()); //-t
        audioParameter.setCut(requestAudioParameter.getExtractThumbnail());
        audioParameter.setMetadata(requestAudioParameter.getExtractMetadata());
    }

    /**
     *
     * @param audioParameter
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    private List<String> execute(final AudioParameter audioParameter) throws InterruptedException, ExecutionException, IOException, NoSuchAlgorithmException {
        Executor executor = new Executor();
        ICommand audioModel = new AudioModel();
        return executor.executeCommandsList(audioModel.createCommand(audioParameter));
    }
}
