const w = 'w=600&h=400&fit=crop'

export const categoryImages = {
  Sedan: `https://images.unsplash.com/photo-1503376780353-7e6692767b70?${w}`,
  SUV: `https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?${w}`,
  Coupe: `https://images.unsplash.com/photo-1583121274602-3e2820c69888?${w}`,
  Truck: `https://images.unsplash.com/photo-1506891536236-3e07892564b7?${w}`,
  Convertible: `https://images.unsplash.com/photo-1553440569-bcc63803a83d?${w}`,
  Hatchback: `https://images.unsplash.com/photo-1568844293986-8d0400bd4745?${w}`,
  Van: `https://images.unsplash.com/photo-1570125909232-eb263c188f7e?${w}`,
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
