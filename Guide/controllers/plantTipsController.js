const axios = require('axios');

exports.getPlantTips = async (req, res) => {
  try {
    const tips = await scrapePlantTips();
    res.status(200).json(tips);
  } catch (error) {
    res.status(500).json({ message: 'Error fetching plant tips' });
  }
};

const scrapePlantTips = async () => {
  return [
    {
      title: "Cara Mengatasi Penyakit Bercak Hitam pada Daun",
      url: "https://id.wikihow.com/Mengatasi-Penyakit-Bercak-Hitam-pada-Daun",
      photoUrl: "https://id.wikihow.com/Mengatasi-Penyakit-Bercak-Hitam-pada-Daun#/Berkas:Deal-with-Black-Spot-Leaf-Disease-Step-1-Version-3.jpg",
      desc: "Penyakit bercak hitam pada daun diawali dengan munculnya bintik-bintik hitam di daun, yang diikuti lingkaran kuning ketika bintik tersebut tumbuh, hingga seluruh bagian daun menjadi kuning dan rontok. Jika tidak ditangani, penyakit ini akan cepat menyebar dan melemahkan tanaman. Jamur yang tinggal di dalam tanah membuat penyakit bercak hitam selalu ada setiap saat. Perawatan yang tepat bisa mengurangi serangan penyakit secara signifikan.",
      plant: "General"
    },
    {
      title: "13 Cara Merawat Tanaman agar Tumbuh Subur",
      url: "https://www.ruparupa.com/blog/cara-merawat-tanaman/",
      photoUrl: "https://www.ruparupa.com/blog/wp-content/uploads/2021/08/cara-merawat-tanaman.jpeg",
      desc: "Mempelajari cara merawat tanaman dapat menjadi aktivitas yang asyik untuk dilakukan. Saat kamu berhasil membuat taman yang indah dan subur, tentunya akan sangat bahagia, bukan ?",
      plant: "Plant 1"
    }
  ];
};
