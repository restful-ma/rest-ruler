import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WekaClassifierModel {

    private static final Logger LOGGER = Logger.getLogger("WekaClassifierModel");

    public static void main(String[] args) {
        WekaClassifier wt = new WekaClassifier();
        final String MODEL = "./request_model.dat";

        try {
            if (new File(MODEL).exists()) {
                LOGGER.info("Model exists. Loading model.");
                wt.loadModel(MODEL);
            } else {
                LOGGER.info("No existing model found. Training a new model.");

                wt.trainClassifier();
                wt.saveModel(MODEL);

                // Perform 10-fold cross-validation
                LOGGER.info("Model trained and saved. Now performing 10-fold cross-validation.");
                wt.performCrossValidation(10);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An error occurred: " + e.getMessage(), e);
        }
    }

}
