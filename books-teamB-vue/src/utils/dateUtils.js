export function getRelativeTime(date) {
  if (!date) return '';

  const now = new Date();
  const past = new Date(date);
  const diffInSeconds = Math.floor((now - past) / 1000);

  // Moins d'une minute
  if (diffInSeconds < 60) {
    return 'il y a moins d\'une minute';
  }

  // Minutes
  const diffInMinutes = Math.floor(diffInSeconds / 60);
  if (diffInMinutes < 60) {
    return diffInMinutes === 1 
      ? 'il y a 1 minute' 
      : `il y a ${diffInMinutes} minutes`;
  }

  // Heures
  const diffInHours = Math.floor(diffInMinutes / 60);
  if (diffInHours < 24) {
    return diffInHours === 1 
      ? 'il y a 1 heure' 
      : `il y a ${diffInHours} heures`;
  }

  // Jours
  const diffInDays = Math.floor(diffInHours / 24);
  if (diffInDays < 7) {
    return diffInDays === 1 
      ? 'il y a 1 jour' 
      : `il y a ${diffInDays} jours`;
  }

  // Semaines
  const diffInWeeks = Math.floor(diffInDays / 7);
  if (diffInWeeks < 4) {
    return diffInWeeks === 1 
      ? 'il y a 1 semaine' 
      : `il y a ${diffInWeeks} semaines`;
  }

  // Mois
  const diffInMonths = Math.floor(diffInDays / 30);
  if (diffInMonths < 12) {
    return diffInMonths === 1 
      ? 'il y a 1 mois' 
      : `il y a ${diffInMonths} mois`;
  }

  // Années
  const diffInYears = Math.floor(diffInMonths / 12);
  return diffInYears === 1 
    ? 'il y a 1 an' 
    : `il y a ${diffInYears} ans`;
}
