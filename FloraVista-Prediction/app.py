import os
from flask import Flask, request, jsonify
from flask_cors import CORS
import tensorflow as tf
import numpy as np
from tensorflow.keras.preprocessing import image
import io
from PIL import Image
from tensorflow.keras.applications.mobilenet_v2 import preprocess_input

# Nonaktifkan optimisasi oneDNN
os.environ['TF_ENABLE_ONEDNN_OPTS'] = '0'

app = Flask(__name__)
CORS(app)

# Lokasi dan nama file model tanaman
plant_model_dir = "model"
plant_model_filename = "model_plant_classification.h5"
plant_model_path = os.path.join(plant_model_dir, plant_model_filename)

# Lokasi dan nama file model penyakit tanaman
disease_model_dir = "model"
disease_model_filename = "model_disease_full.h5"
disease_model_path = os.path.join(disease_model_dir, disease_model_filename)

# Lokasi dan nama file model tanah
soil_model_dir = "model"
soil_model_filename = "model_soil_classification.h5"
soil_model_path = os.path.join(soil_model_dir, soil_model_filename)

# Variabel global untuk model tanaman, penyakit, dan tanah
plant_model = None
disease_model = None
soil_model = None

# Informasi tanaman
plant_info = {
    'APPLE': {
        'Family': "Rosaceae",
        'Description': "Buah manis yang dapat dimakan yang dihasilkan oleh pohon apel.",
        'Benefit': ["1. Tinggi serat", "2. Kaya akan vitamin C", "3. Membantu mengatur gula darah"]
    },
    'BLUEBERRY': {
        'Family': "Ericaceae",
        'Description': "Buah kecil berbentuk bulat yang biasanya berwarna biru atau ungu.",
        'Benefit': ["1. Kaya akan antioksidan", "2. Mendukung kesehatan jantung", "3. Meningkatkan fungsi otak"]
    },
    'CHERRY': {
        'Family': "Rosaceae",
        'Description': "Buah batu kecil berbentuk bulat yang biasanya berwarna merah atau hitam.",
        'Benefit': ["1. Sifat anti-inflamasi", "2. Membantu tidur", "3. Baik untuk kesehatan jantung"]
    },
    'CORN': {
        'Family': "Poaceae",
        'Description': "Tanaman sereal yang menghasilkan biji besar, atau jagung, disusun dalam barisan pada tongkolnya.",
        'Benefit': ["1. Tinggi serat", "2. Sumber antioksidan yang baik", "3. Mendukung kesehatan pencernaan"]
    },
    'GRAPE': {
        'Family': "Vitaceae",
        'Description': "Buah, secara botani berupa berry, dari menjalar kayu anggur berbunga dari genus Vitis.",
        'Benefit': ["1. Tinggi vitamin", "2. Baik untuk kesehatan jantung", "3. Mendukung fungsi kekebalan tubuh"]
    },
    'PEACH': {
        'Family': "Rosaceae",
        'Description': "Buah batu lunak, juicy, dan daging.",
        'Benefit': ["1. Kaya akan vitamin", "2. Mendukung kesehatan kulit", "3. Membantu pencernaan"]
    },
    'PEPPER': {
        'Family': "Solanaceae",
        'Description': "Buah dari tanaman capsicum, umumnya digunakan sebagai rempah.",
        'Benefit': ["1. Tinggi vitamin", "2. Mengandung antioksidan", "3. Mendukung kesehatan metabolisme"]
    },
    'POTATO': {
        'Family': "Solanaceae",
        'Description': "Umbi tanaman kentang yang mengandung pati, salah satu tanaman pangan paling penting.",
        'Benefit': ["1. Kaya nutrisi", "2. Mendukung kesehatan pencernaan", "3. Memberikan energi"]
    },
    'RASPBERRY': {
        'Family': "Rosaceae",
        'Description': "Buah yang dapat dimakan dari berbagai spesies tanaman dalam genus Rubus.",
        'Benefit': ["1. Tinggi serat", "2. Kaya akan vitamin", "3. Mendukung kesehatan kekebalan tubuh"]
    },
    'SOYBEAN': {
        'Family': "Fabaceae",
        'Description': "Spesies polong yang berasal dari Asia Timur, banyak dibudidayakan untuk kacangnya yang dapat dimakan.",
        'Benefit': ["1. Tinggi protein", "2. Mendukung kesehatan jantung", "3. Baik untuk kesehatan tulang"]
    },
    'STRAWBERRY': {
        'Family': "Rosaceae",
        'Description': "Buah merah manis, lembut, dengan permukaan berbintik biji.",
        'Benefit': ["1. Tinggi antioksidan", "2. Mendukung kesehatan jantung", "3. Meningkatkan sistem kekebalan tubuh"]
    },
    'TOMATO': {
        'Family': "Solanaceae",
        'Description': "Buah edibel berwarna merah mengkilap, atau kadang kuning, dengan daging yang pulpy.",
        'Benefit': ["1. Kaya akan vitamin", "2. Mendukung kesehatan jantung", "3. Baik untuk kesehatan kulit"]
    }
}

# Informasi penyakit tanaman
disease_info = {
    'BACTERIAL SPOT': {
        'Description': "Penyakit yang disebabkan oleh bakteri pada daun dan buah tanaman, umumnya menyebabkan bercak berwarna gelap.",
        'Treatment': "Pengendalian dengan menggunakan fungisida bakterisida, pengelolaan tanaman yang baik, dan praktik sanitasi."
    },
    'BLACK MEASLES': {
        'Description': "Penyakit yang disebabkan oleh jamur, terlihat sebagai bercak-bercak kecil berwarna coklat atau hitam pada daun.",
        'Treatment': "Pengendalian dengan menyemprotkan fungisida, menjaga kebersihan lingkungan, dan menghindari kelembaban berlebih."
    },
    'BLACK ROT': {
        'Description': "Penyakit yang disebabkan oleh jamur pada tanaman cruciferous, biasanya terlihat sebagai bercak-bercak hitam pada daun.",
        'Treatment': "Pengendalian dengan menjaga kebersihan, menggunakan bibit yang bebas penyakit, dan menghindari kondisi lembab."
    },
    'CITRUS GREENING': {
        'Description': "Penyakit serius pada tanaman jeruk yang disebabkan oleh bakteri, terlihat sebagai daun menguning dan pertumbuhan tidak normal.",
        'Treatment': "Pengendalian dengan mengelola serangga penular vektor, pemangkasan, dan aplikasi antibiotik tertentu."
    },
    'LEAF BLIGHT': {
        'Description': "Penyakit yang disebabkan oleh jamur pada daun tanaman, terlihat sebagai bercak berwarna coklat atau abu-abu pada daun.",
        'Treatment': "Pengendalian dengan menyemprotkan fungisida, menghilangkan daun yang terinfeksi, dan menjaga kebersihan."
    },
    'LEAF MOLD': {
        'Description': "Penyakit yang disebabkan oleh jamur pada daun, umumnya terlihat sebagai bercak putih keabu-abuan pada permukaan daun.",
        'Treatment': "Pengendalian dengan memastikan sirkulasi udara yang baik, menghilangkan daun yang terinfeksi, dan mengelola kelembaban."
    },
    'LEAF SCORCH': {
        'Description': "Penyakit yang menyebabkan daun mengering dan menguning, sering disebabkan oleh bakteri atau jamur patogen.",
        'Treatment': "Pengendalian dengan pengelolaan air yang baik, penyemprotan fungisida, dan memangkas tanaman."
    },
    'LEAF SPOT': {
        'Description': "Penyakit yang menyebabkan bercak berwarna gelap atau coklat pada daun, disebabkan oleh jamur atau bakteri.",
        'Treatment': "Pengendalian dengan menjaga kebersihan, menyemprotkan fungisida, dan mengatur irigasi."
    },
    'MOSAIC VIRUS': {
        'Description': "Penyakit virus yang umum pada tanaman, terlihat sebagai daun menguning dengan pola mosaik atau bercak.",
        'Treatment': "Pengendalian dengan menggunakan bibit bebas virus, mengelola serangga vektor, dan menghilangkan tanaman yang terinfeksi."
    },
    'POWDERY MILDEW': {
        'Description': "Penyakit jamur yang terlihat sebagai serbuk putih pada daun dan bagian tanaman lainnya.",
        'Treatment': "Pengendalian dengan menyemprotkan fungisida, menjaga sirkulasi udara yang baik, dan menjaga tanaman tetap kering."
    },
    'RUST': {
        'Description': "Penyakit yang disebabkan oleh jamur, terlihat sebagai bercak-bercak berwarna coklat atau oranye pada daun dan batang.",
        'Treatment': "Pengendalian dengan menggunakan bibit resisten, menyemprotkan fungisida, dan menjaga kebersihan."
    },
    'SCAB': {
        'Description': "Penyakit jamur yang menyebabkan bercak berwarna gelap atau abu-abu pada daun, buah, dan batang tanaman.",
        'Treatment': "Pengendalian dengan menjaga kebersihan, menggunakan bibit bebas penyakit, dan menyemprotkan fungisida."
    },
    'SPIDER MITES': {
        'Description': "Hama kecil yang menyebabkan kerusakan pada tanaman dengan membuat jaringan halus di bawah daun.",
        'Treatment': "Pengendalian dengan menyemprotkan insektisida, menjaga kelembaban udara, dan membuang daun yang terinfeksi."
    },
    'TARGET SPOT': {
        'Description': "Penyakit yang disebabkan oleh jamur pada daun tanaman, terlihat sebagai bercak berwarna gelap dengan tepi merah.",
        'Treatment': "Pengendalian dengan menyemprotkan fungisida, menjaga sirkulasi udara yang baik, dan memangkas tanaman."
    },
    'YELLOW LEAF CURL VIRUS': {
        'Description': "Penyakit virus yang menyebabkan daun tanaman menguning, keriput, dan berkumpul.",
        'Treatment': "Pengendalian dengan menggunakan bibit bebas virus, mengelola serangga vektor, dan menghilangkan tanaman yang terinfeksi."
    }
}

# Informasi tanah
soil_info = {
    'ALUVIAL': {
        'Description': "Tanah aluvial merupakan tanah yang terbentuk dari endapan sungai atau aliran air. Umumnya sangat subur dan kaya akan unsur hara.",
        'Suitable_Plants': ["Padi", "Jagung", "Sayuran berdaun hijau seperti bayam dan kangkung"]
    },
    'CHERNOZEM': {
        'Description': "Tanah chernozem adalah tanah yang sangat subur dan gelap, biasanya ditemukan di daerah beriklim sedang hingga dingin.",
        'Suitable_Plants': ["Gandum", "Barley", "Kentang", "Kubis", "Bit"]
    },
    'CLAY': {
        'Description': "Tanah lempung kaya akan partikel halus dan cenderung tahan terhadap erosi.",
        'Suitable_Plants': ["Kacang-kacangan seperti kacang tanah, kacang hijau", "Tanaman buah seperti apel dan pir"]
    },
    'GRUMUSOL': {
        'Description': "Tanah grumusol memiliki struktur agak gumpalan, umumnya subur dan baik untuk pertanian.",
        'Suitable_Plants': ["Kedelai", "Kacang merah", "Tanaman pangan lainnya seperti sorgum dan kacang hijau"]
    },
    'LATERITE': {
        'Description': "Tanah laterit adalah tanah yang umumnya kering dan kaya akan besi oksida.",
        'Suitable_Plants': ["Tanaman yang tahan kekeringan seperti jeruk, mangga, dan tanaman keras lainnya"]
    },
    'ORGANOSOL': {
        'Description': "Tanah organosol kaya akan bahan organik yang terakumulasi secara alami.",
        'Suitable_Plants': ["Tanaman buah-buahan seperti pisang, pepaya", "Tanaman sayuran seperti tomat dan cabai"]
    },
    'PODSOLIC': {
        'Description': "Tanah podsolik cenderung masam dengan akumulasi bahan organik di bagian atas dan mineral lebih rendah di bawahnya.",
        'Suitable_Plants': ["Blueberry", "Cranberry", "Kayu manis", "Tanaman hutan lainnya"]
    },
    'SANDY': {
        'Description': "Tanah pasir memiliki partikel besar dan sedikit bahan organik, cenderung memiliki drainase yang baik.",
        'Suitable_Plants': ["Kaktus", "Zaitun", "Lavender", "Tanaman lain yang tahan terhadap kondisi kering"]
    }
}

# Daftar kelas tanaman, penyakit, dan tanah
class_names_plant = list(plant_info.keys())
class_names_disease = list(disease_info.keys())
class_names_soil = list(soil_info.keys())

def load_and_process_image(image_data, target_size=(224, 224)):
    img = Image.open(image_data)
    img = img.resize(target_size)
    img_array = image.img_to_array(img)
    img_array = np.expand_dims(img_array, axis=0)
    img_array = preprocess_input(img_array)
    return img_array

@app.route('/predict/plant', methods=['POST'])
def predict_plant():
    global plant_model
    try:
        if 'image' not in request.files:
            return jsonify({'error': 'Data gambar tidak ditemukan'}), 400

        image_file = request.files['image']
        image_data = io.BytesIO(image_file.read())
        img_array = load_and_process_image(image_data)

        if plant_model is None:
            # Muat model tanaman jika belum dimuat
            if os.path.exists(plant_model_path):
                plant_model = tf.keras.models.load_model(plant_model_path)  # Memuat model dari file
                print(f"Model tanaman berhasil dimuat dari {plant_model_path}")
            else:
                return jsonify({'error': f'File model tanaman tidak ditemukan di {plant_model_path}'}), 500

        # Lakukan prediksi
        predictions = plant_model.predict(img_array)
        predicted_class = tf.argmax(predictions[0]).numpy()
        confidence = predictions[0][predicted_class]
        is_above_threshold = bool(confidence > 0.5)

        # Dapatkan informasi tanaman
        predicted_plant = class_names_plant[predicted_class]
        plant_details = plant_info[predicted_plant]

        # Kembalikan hasil prediksi
        return jsonify({
            'message': 'Prediksi tanaman berhasil.',
            'data': {
                'hasil': predicted_plant,
                'skorKepercayaan': float(confidence * 100),
                'isAboveThreshold': is_above_threshold,
                'Family': plant_details['Family'],
                'Description': plant_details['Description'],
                'Benefit': plant_details['Benefit']
            }
        }), 200

    except Exception as e:
        return jsonify({'error': str(e)}), 400

@app.route('/predict/disease', methods=['POST'])
def predict_disease():
    global disease_model
    try:
        if 'image' not in request.files:
            return jsonify({'error': 'Data gambar tidak ditemukan'}), 400

        image_file = request.files['image']
        image_data = io.BytesIO(image_file.read())
        img_array = load_and_process_image(image_data)

        if disease_model is None:
            # Muat model penyakit jika belum dimuat
            if os.path.exists(disease_model_path):
                disease_model = tf.keras.models.load_model(disease_model_path)  # Memuat model dari file
                print(f"Model penyakit berhasil dimuat dari {disease_model_path}")
            else:
                return jsonify({'error': f'File model penyakit tidak ditemukan di {disease_model_path}'}), 500

        # Lakukan prediksi
        predictions = disease_model.predict(img_array)
        predicted_class = tf.argmax(predictions[0]).numpy()
        confidence = predictions[0][predicted_class]
        is_above_threshold = bool(confidence > 0.5)

        # Dapatkan informasi penyakit tanaman
        predicted_disease = class_names_disease[predicted_class]
        disease_details = disease_info[predicted_disease]

        # Kembalikan hasil prediksi
        return jsonify({
            'message': 'Prediksi penyakit tanaman berhasil.',
            'data': {
                'hasil': predicted_disease,
                'skorKepercayaan': float(confidence * 100),
                'isAboveThreshold': is_above_threshold,
                'Description': disease_details['Description'],
                'Treatment': disease_details['Treatment']
            }
        }), 200

    except Exception as e:
        return jsonify({'error': str(e)}), 400

@app.route('/predict/soil', methods=['POST'])
def predict_soil():
    global soil_model
    try:
        if 'image' not in request.files:
            return jsonify({'error': 'Data gambar tidak ditemukan'}), 400

        image_file = request.files['image']
        image_data = io.BytesIO(image_file.read())
        img_array = load_and_process_image(image_data)

        if soil_model is None:
            # Muat model tanah jika belum dimuat
            if os.path.exists(soil_model_path):
                soil_model = tf.keras.models.load_model(soil_model_path)  # Memuat model dari file
                print(f"Model tanah berhasil dimuat dari {soil_model_path}")
            else:
                return jsonify({'error': f'File model tanah tidak ditemukan di {soil_model_path}'}), 500

        # Lakukan prediksi
        predictions = soil_model.predict(img_array)
        predicted_class = tf.argmax(predictions[0]).numpy()
        confidence = predictions[0][predicted_class]
        is_above_threshold = bool(confidence > 0.5)

        # Dapatkan informasi tanah
        predicted_soil = class_names_soil[predicted_class]
        soil_details = soil_info[predicted_soil]

        # Kembalikan hasil prediksi
        return jsonify({
            'message': 'Prediksi jenis tanah berhasil.',
            'data': {
                'hasil': predicted_soil,
                'skorKepercayaan': float(confidence * 100),
                'isAboveThreshold': is_above_threshold,
                'Description': soil_details['Description'],
                'Suitable_Plants': soil_details['Suitable_Plants']
            }
        }), 200

    except Exception as e:
        return jsonify({'error': str(e)}), 400

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=int(os.environ.get("PORT", 8080)))