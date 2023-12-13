pipelineJob('ExampleDSLJob') {
    description('This is an example DSL job')

    parameters {
        // Define parameters for the job
        stringParam('USERNAME', '', 'Enter your username')
        booleanParam('ENABLE_FEATURE', true, 'Enable feature')
        choice(name: 'CHOICE_PARAM', choices: ['Option A', 'Option B', 'Option C'], description: 'Choose an option')
        // Active Choices parameter
        [$class: 'CascadeChoiceParameter', 
            choiceType: 'PT_SINGLE_SELECT', 
            description: 'Select an option', 
            filterLength: 1, 
            filterable: false, 
            name: 'DynamicChoices', 
            referencedParameters: '', 
            script: [
                $class: 'GroovyScript', 
                fallbackScript: 'return ["Error: Unable to fetch data"]', 
                script: [
                    $class: 'GroovyScript', 
                    script: 'return calculateChoices()', // Call the method to generate the list
                ],
            ],
        ]
    }

    definition {
        cps {
            script("""
                // Your pipeline script here
                node {
                    stage('Example Stage') {
                        echo "Hello, Jenkins DSL!"
                        // Use parameters in your pipeline
                        echo "Username: \${params.USERNAME}"
                        echo "Feature enabled: \${params.ENABLE_FEATURE}"
                        echo "Chosen option: \${params.CHOICE_PARAM}"
                        // Use the Active Choices parameter
                        echo "Dynamic choice: \${params.DynamicChoices}"
                    }
                }
            """)
        }
    }
}

// Method to generate choices dynamically
def calculateChoices() {
    def list = [] // Initialize an empty list
    // Generate values dynamically (replace this with your logic)
    for (int i = 1; i <= 10; i++) {
        list.add("Option ${i}") // Add options to the list
    }
    return list // Return the generated list
}
