class Medicine(object):
    def __init__(self, identification_name):
        self.identification_name = identification_name
        self.brand_names = set()
        self.dosages = []

    def __repr__(self):
        return self.identification_name

    def add_dosage(self, dosage_type_str, dosage_amount_str):
        if dosage_type_str and dosage_amount_str:
            self.dosages.append((dosage_type_str, dosage_amount_str))

    def add_brand_name(self, brand_name):
        if brand_name:
            self.brand_names.add(brand_name)
