import json
import os
import unittest

from src.executors.extract_json_text import extract_medical_reports_to_json


class ExtractMedicalReportsToJsonTests(unittest.TestCase):
    test_report_names = ['tests/test_files/test_report_0.txt',
                         'tests/test_files/test_report_1.txt',
                         'tests/test_files/test_report_2.txt']
    output_test_filename = 'tests/test_files/output.json'

    def test_json(self):
        '''
        Tests generated json to make sure it can be loaded
        '''
        extract_medical_reports_to_json(self.test_report_names, self.output_test_filename)

        with open(self.output_test_filename, 'rb') as input_json_file:
            patients = [json.loads(json_line) for json_line in input_json_file]

            # test to make sure extracted three patients
            self.assertEqual(len(patients), 3)

        os.remove(self.output_test_filename)


if __name__ == '__main__':
    unittest.main()
