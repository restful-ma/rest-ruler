package cli.weka;

import org.apache.commons.lang3.tuple.ImmutablePair;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

// import java.net.URL;

/**
 * Request methods classifier based Weka libs, for detect transaction direction.
 *
 * @see http://https://waikato.github.io/weka-wiki/use_weka_in_your_java_code/
 * @see https://www.cs.waikato.ac.nz/ml/index.html
 */
public class RequestMethodsWekaClassifier {
    Logger LOGGER = Logger.getLogger("RequestMethodsService");
    private FilteredClassifier classifier;
    // declare attributes of Instance
    private ArrayList<Attribute> wekaAttributes;

    public RequestMethodsWekaClassifier() {

        /*
         * Class for running an arbitrary classifier on data that has been passed
         * through an arbitrary filter.
         * Like the classifier, the structure of the filter is based exclusively on the
         * training data and test
         * instances will be processed by the filter without changing their structure.
         * If unequal instance weights or attribute weights are present,
         * and the filter or the classifier are unable to deal with them,
         * the instances and/or attributes are resampled with replacement
         * based on the weights before they are passed to the filter or the classifier
         * (as appropriate).
         */
        classifier = new FilteredClassifier();

        /**
         * Class for building and using a multinomial Naive Bayes classifier. For more
         * information see,
         *
         * Andrew Mccallum, Kamal Nigam: A Comparison of Event Models for Naive Bayes
         * Text Classification.
         * In: AAAI-98 Workshop on 'Learning for Text Categorization', 1998.
         * https://weka.sourceforge.io/doc.dev/weka/classifiers/bayes/NaiveBayesMultinomial.html
         */
        classifier.setClassifier(new NaiveBayesMultinomial());

        // Declare text attribute to hold the message
        Attribute attributeText = new Attribute("text", (List<String>) null);

        /**
         * Declare the label attribute along with its values
         */
        ArrayList<String> classAttributeValues = new ArrayList<>();
        classAttributeValues.add("get");
        classAttributeValues.add("post");
        classAttributeValues.add("put");
        classAttributeValues.add("delete");
        classAttributeValues.add("invalid");
        Attribute classAttribute = new Attribute("label_type", classAttributeValues);

        /**
         * Built the feature vector "wekaAttributes"
         */
        wekaAttributes = new ArrayList<>();
        wekaAttributes.add(classAttribute);
        wekaAttributes.add(attributeText);

    }

    /**
     * classify a new message into income or outcome.
     *
     * @param text to be classified.
     * @return a class label (income or outcome )
     */
    public ImmutablePair<String, Double> predict(String text) {
        try {
            // create new Instance for prediction.
            DenseInstance newinstance = new DenseInstance(2);

            // weka demand a dataset to be set to new Instance
            Instances newDataset = new Instances("predictiondata", wekaAttributes, 1);
            newDataset.setClassIndex(0);

            newinstance.setDataset(newDataset);

            // text attribute value set to value to be predicted
            newinstance.setValue(wekaAttributes.get(1), text);

            // predict most likely class for the instance
            double prediction = classifier.classifyInstance(newinstance);
            String predictionValue = newDataset.classAttribute().value((int) prediction);

            // Percentage of the prediction
            double[] percentage = classifier.distributionForInstance(newinstance);
            double percentageOfPredictedValue = percentage[(int) prediction];

            // Create pair containing the prediction value and the percentage of accuracy
            ImmutablePair<String, Double> predictionValues = new ImmutablePair<>(predictionValue,
                    percentageOfPredictedValue);

            // return original label
            return predictionValues;
        } catch (Exception e) {
            LOGGER.warning(e.getMessage());
            return null;
        }
    }

    /**
     * Model loader
     *
     * @param filename The name of the file that stores the text.
     */
    public void loadModel(String filename) {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename));
            Object tmp = in.readObject();
            classifier = (FilteredClassifier) tmp;
            in.close();
        } catch (FileNotFoundException e) {
            LOGGER.warning(e.getMessage());
        } catch (IOException e) {
            LOGGER.warning(e.getMessage());
        } catch (ClassNotFoundException e) {
            LOGGER.warning(e.getMessage());
        }
    }

}
