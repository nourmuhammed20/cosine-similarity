Cosine Similarity - Information Retrival
================================================
🔎 Positional Index and Cosine Similarity

Description
-----------

This project is designed to read text files, build an inverted index, and perform cosine similarity calculations to rank files based on their similarity to a given query. The project utilizes web crawling with JSoup to gather data from Wikipedia pages, with options to limit the crawl depth. Multi-threading is employed to enhance performance, ensuring faster content retrieval from Wikipedia pages.

Features
--------

✅ Read 10 text files  
✅ Build the inverted index for the 10 files  
✅ Accept user queries consisting of a set of words  
✅ Compute the cosine similarity between each file and the query  
✅ Rank the 10 files based on cosine similarity values

Installation
------------

1.  Clone the repository:
    
    bash
    
    ```bash
    git clone https://github.com/your-username/repository-name.git
    ```
    
2.  Change to the project directory:
    
    bash
    
    ```bash
    cd repository-name
    ```
    
3.  Install the required dependencies:
    
    bash
    
    ```bash
    pip install -r requirements.txt
    ```
    
4.  Update the path of the folder and files in the `config.py` file according to your system:
    
    python
    
    ```python
    FOLDER_PATH = '/path/to/folder'  # Update folder path
    FILENAMES = ['file1.txt', 'file2.txt', ..., 'file10.txt']  # Update file names
    ```
    

Usage
-----

1.  Run the main script:
    
    bash
    
    ```bash
    python main.py
    ```
    
2.  Follow the prompts to initiate the process.
    

Configuration
-------------

You can modify the following parameters in the `main.java` file:

*   `NUM_FILES`: Number of text files to read (default: 10)
*   `CRAWL_DEPTH`: Depth of web crawling (default: 2)
*   `NUM_THREADS`: Number of threads for multi-threading (default: 4)

Contributing
------------

Contributions are welcome! If you encounter any bugs or have suggestions for improvements, please open an issue or submit a pull request.

License
-------

This project is licensed under the [MIT License](LICENSE).

Acknowledgments
---------------

The implementation of web crawling was inspired by [JSOUP library]([https://github.com/xyz/xyz-library](https://github.com/jhy/jsoup)).
