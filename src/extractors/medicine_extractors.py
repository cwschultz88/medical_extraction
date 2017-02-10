import os
import subprocess

from src.extractors.info_extractor_interface import IInfoExtractor


class MedexExtractor(IInfoExtractor):
    @classmethod
    def extract_info(cls, medical_reports):
        '''
        should return a dictionary of medicine metioned

        Interfaces with the Medex UIMA library
        '''
        # store medical report contents in files
        for i, medical_report in enumerate(medical_reports):
            with open('libs/Medex_UIMA_1.2.1/input/report_' + str(i) + '.txt', 'w') as input_file:
                input_file.write(medical_report.report_contents)

        # run Medex UIMA through a seperate process
        original_working_directory = os.getcwd()
        os.chdir('libs/Medex_UIMA_1.2.1')
        print 'Running Medex UIMA - Text outputted is related to medex program'
        subprocess.call('java -Xmx1024m -cp lib/*:bin org.apache.medex.Main -i input -o output', shell=True)
        os.chdir(original_working_directory)

        # read in output from medex and store results
        medicines_in_reports = []
        for i in xrange(len(medical_reports)):
            medicines_in_this_report = {}
            medicines_in_reports.append(medicines_in_this_report)
            with open('libs/Medex_UIMA_1.2.1/output/report_' + str(i) + '.txt', 'r') as output_file:
                for medicine_tagged_line in output_file:
                    split_medicine_line = medicine_tagged_line.split('|')

                    medicine_name = split_medicine_line[-1].strip()

                    brand_name = split_medicine_line[1].strip()
                    brand_name = brand_name[:brand_name.find('[')]

        # clean up medex input and output files
        for i in xrange(len(medical_reports)):
            os.remove('libs/Medex_UIMA_1.2.1/input/report_' + str(i) + '.txt')
            os.remove('libs/Medex_UIMA_1.2.1/output/report_' + str(i) + '.txt')
            os.remove('libs/Medex_UIMA_1.2.1/log/report_' + str(i) + '.txt')
            os.remove('libs/Medex_UIMA_1.2.1/sents/report_' + str(i) + '.txt')