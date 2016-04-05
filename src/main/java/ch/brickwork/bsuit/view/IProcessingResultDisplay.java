package ch.brickwork.bsuit.view;


import ch.brickwork.bsuit.interpreter.interpreters.ProcessingResult;

/**
 * component able to display a processing result and/or tables or views
 * <p/>
 * User: marcel
 * Date: 5/28/13
 * Time: 2:33 PM
 */
public interface IProcessingResultDisplay {

    void displayProcessingResult(ProcessingResult processingResult);

    void displayTableOrView(String tableOrViewName, String variableName, String sortField, Boolean sortAsc);

    enum RUN_STATUS {
        READY, BUSY
    };

    /**
     * many output displays will display the status of operation during execution of a process
     * @param runStatus
     */
    void setRunStatus(RUN_STATUS runStatus);
}
