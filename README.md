# Extracting Patient Medical Information out of Unstructured Medical Reports

Extracts patient information from unstructured medical report files. Currently supports medical reports in the following formats:
   1. txt


Output Results in JSON Format

Written on Mac OS - Code should work in a Linux environment

Assumes all python scripts are called from the root of the repo, i.e. this directory level.

To run the json extraction example:
```
python src/executors/extract_json_text.py input output.json
```

To run a unit test example:
```
python -m unittest tests.data.input_data_extractors_tests
```

Must have java >= 1.7 on machine
