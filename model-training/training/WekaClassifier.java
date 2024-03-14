import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;
import weka.core.converters.ArffSaver;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WekaClassifier {

    private static final Logger LOGGER = Logger.getLogger(WekaClassifier.class.getName());

    private FilteredClassifier classifier;
    private Instances trainData;
    private Instances predictionDataSet;
    private static final String TRAIN_ARFF = "dataset/train_income_outcome.arff";
    private static final String TEST_ARFF = "dataset/test_income_outcome.arff";
    private ArrayList<Attribute> wekaAttributes;

    public WekaClassifier() {
        initializeClassifier();
        initializeAttributes();
    }

    private void initializeClassifier() {
        classifier = new FilteredClassifier();
        classifier.setClassifier(new NaiveBayesMultinomial());

        StringToWordVector filter = new StringToWordVector();
        filter.setAttributeIndices("last");

        NGramTokenizer tokenizer = new NGramTokenizer();
        tokenizer.setNGramMinSize(1);
        tokenizer.setNGramMaxSize(2);
        tokenizer.setDelimiters("\\W");
        filter.setTokenizer(tokenizer);
        filter.setLowerCaseTokens(true);

        classifier.setFilter(filter);
    }

    private void initializeAttributes() {
        Attribute attributeText = new Attribute("text", (List<String>) null);
        ArrayList<String> classAttributeValues = new ArrayList<>(List.of("get", "post", "put", "delete", "invalid"));
        Attribute classAttribute = new Attribute("label_type", classAttributeValues);

        wekaAttributes = new ArrayList<>(List.of(classAttribute, attributeText));
        predictionDataSet = new Instances("predictiondata", wekaAttributes, 0);
        predictionDataSet.setClassIndex(0);
    }

    public void loadAndPrepareData(String trainFile, String testFile) {
        try {
            trainData = loadDataset(trainFile);
            saveArff(trainData, TRAIN_ARFF);
            trainData.setClassIndex(0);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error loading training data: " + e.getMessage(), e);
        }

        try {
            Instances testData = loadDataset(testFile);
            saveArff(testData, TEST_ARFF);
            testData.setClassIndex(0);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error loading test data: " + e.getMessage(), e);
        }
    }

    public void trainClassifier() {
        try {
            if (trainData != null) {
                classifier.buildClassifier(trainData);
            } else {
                LOGGER.warning("Training data is not initialized.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error during classifier training: " + e.getMessage(), e);
        }
    }

    public String predict(String text) {
        try {
            predictionDataSet.clear();
            DenseInstance newInstance = new DenseInstance(2);
            newInstance.setValue(wekaAttributes.get(1), text);
            newInstance.setDataset(predictionDataSet);

            double pred = classifier.classifyInstance(newInstance);
            return predictionDataSet.classAttribute().value((int) pred);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error during prediction: " + e.getMessage(), e);
            return null;
        }
    }

    public String evaluate(String testArffFile) {
        try {
            Instances testData = loadArff(testArffFile);
            testData.setClassIndex(0);
            Evaluation eval = new Evaluation(testData);
            eval.evaluateModel(classifier, testData);

            return eval.toSummaryString("\nResults\n======\n", false);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error during evaluation: " + e.getMessage(), e);
            return null;
        }
    }

    public void saveModel(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(classifier);
            LOGGER.info("Saved model: " + filename);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error saving model: " + e.getMessage(), e);
        }
    }

    public void loadModel(String filename) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            classifier = (FilteredClassifier) in.readObject();
            LOGGER.info("Model successfully loaded: " + filename);
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.WARNING, "Error loading model: " + e.getMessage(), e);
        }
    }

    private Instances loadDataset(String filename) throws IOException {
        Instances dataset = new Instances("Dataset", wekaAttributes, 0);
        dataset.setClassIndex(0);

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\s+", 2);
                if (parts.length == 2) {
                    DenseInstance instance = new DenseInstance(2);
                    instance.setValue(wekaAttributes.get(0), parts[0]);
                    instance.setValue(wekaAttributes.get(1), parts[1]);
                    dataset.add(instance);
                }
            }
        }
        return dataset;
    }

    private Instances loadArff(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            ArffReader arff = new ArffReader(reader);
            return arff.getData();
        }
    }

    private void saveArff(Instances dataset, String filename) throws IOException {
        ArffSaver saver = new ArffSaver();
        saver.setInstances(dataset);
        saver.setFile(new File(filename));
        saver.writeBatch();
    }

    public void performCrossValidation(int folds) {
        try {
            if (trainData == null || trainData.numInstances() == 0) {
                LOGGER.warning("Training data is not initialized or is empty.");
                return;
            }

            trainData.setClassIndex(0);
            Evaluation eval = new Evaluation(trainData);
            Random rand = new Random(1);

            // Perform cross-validation
            eval.crossValidateModel(classifier, trainData, folds, rand);

            // Output evaluation results
            LOGGER.info("Results from " + folds + "-fold cross-validation:");
            LOGGER.info(eval.toSummaryString());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Cross-validation error: " + e.getMessage(), e);
        }
    }

}
