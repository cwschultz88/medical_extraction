import argparse
import json
import os
import sys

# Hack solution for import error associated with next import - don't have time to fix it though properly
sys.path.insert(0, os.path.abspath(os.getcwd()))

from src.data.retrieve_medical_report import retrieve_medical_reports_localfiles
from src.extractors.doctor_extractors import DefaultDoctorExtractor as DoctorExtractor
from src.extractors.medicine_extractors import MedexExtractor as MedicineExtractor
from src.extractors.patient_extractors import DefaultPatientExtractor as PatientExtractor


def extract_medical_reports_to_json(medical_report_names, output_filename):
    extractors_for_each_report = {'doctors': DoctorExtractor, 'medicine': MedicineExtractor}
    patients_extracted = []

    medical_reports = retrieve_medical_reports_localfiles(medical_report_names)

    associated_patients = PatientExtractor().extract_info(medical_reports)

    report_extractions = {extraction: extractor.extract_info(medical_reports) for extraction, extractor in extractors_for_each_report.iteritems()}
    for i, patient in enumerate(associated_patients):
        patient.medical_history.add_entry(medical_reports[i].report_date, {extractor:extractions[i] for extractor, extractions in report_extractions.iteritems()})

    with open(output_filename, 'wb') as json_file:
        for i, patient in enumerate(associated_patients):
            if i != 0:
                json_file.write('\n')
            json_file.write(json.dumps(patient, default=lambda o: o.__dict__))


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("input_directory", type=str, help="input directory containing medical reports")
    parser.add_argument("output_json_file", type=str, help="output json file")
    args = parser.parse_args()

    path_to_program = os.path.abspath(os.getcwd())
    input_medical_reports = [os.path.join(path_to_program, args.input_directory, filename) for filename in os.listdir(args.input_directory) if not filename.startswith('.')]

    extract_medical_reports_to_json(input_medical_reports, args.output_json_file)


if __name__ == '__main__':
    main()