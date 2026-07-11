export const categoryImages = {
  Sedan: '/images/Sedan.jpg',
  SUV: '/images/SUV.jpg',
  Coupe: '/images/Coupe.jpg',
  Truck: '/images/Truck.jpg',
  Convertible: '/images/Convertible.jpg',
  Hatchback: '/images/Hatchback.jpg',
  Van: '/images/Van.jpg',
}

export const categoryGradients = {
  Sedan: 'linear-gradient(135deg, #667eea, #764ba2)',
  SUV: 'linear-gradient(135deg, #f093fb, #f5576c)',
  Coupe: 'linear-gradient(135deg, #4facfe, #00f2fe)',
  Truck: 'linear-gradient(135deg, #43e97b, #38f9d7)',
  Hatchback: 'linear-gradient(135deg, #fa709a, #fee140)',
  Van: 'linear-gradient(135deg, #a18cd1, #fbc2eb)',
}

export function getVehicleImage(category) {
  return categoryImages[category] || categoryImages.Sedan
}

export function getVehicleGradient(category) {
  return categoryGradients[category] || categoryGradients.Sedan
}
