class MedicalHistory(object):
    '''
    Stores a patient's medical history
    '''
    def __init__(self):
        self.entries = {}

    def add_entry(self, date, entry_details_dict):
        '''
        Adds to entry ro medical history

        date should be a datetime object
        entry_details_dict should be a dictionary such as:
        {
          medicine: [medicine_obj_0, medicine_obj_1]
          doctors: ['Dr. Somebody', 'Dr. Who']
        }
        '''
        self.entries[str(date)] = entry_details_dict
