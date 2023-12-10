package io.github.libfp.similarity;//@date 25.10.2023

/**
 * The <code>Strategy</code> class represents a configuration and management
 * class for handling various similarity strategies and processing steps. It
 * allows the registration of similarity strategies and processing steps for
 * different types of profiles.
 *
 * <p>
 * The <code>Strategy</code> class uses a collection of strategies and steps
 * organized by their corresponding profile types. It provides methods for
 * registering, retrieving, and managing these strategies and steps.
 * </p>
 *
 * <pre>{@code
 *      Strategy strategy = new Strategy()
 *          .with(Profile.class, new MyProfileSimilarityStrategy())
 *          .with(ClassProfile.class, new MyClassStep())
 *          .with(new Strategy()); // inherit from other strategies
 * }</pre>
 */
public class Strategy
        extends AbstractStrategy<Strategy>
{

    @Override
    protected Strategy self()
    {
        return this;
    }
}
